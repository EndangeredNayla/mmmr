package org.mmmr.services.swing;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.GridLayout;
import java.awt.Transparency;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.image.BufferedImage;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;

import org.mmmr.services.Config;
import org.mmmr.services.Messages;
import org.mmmr.services.NiceFont;
import org.mmmr.services.swing.common.FancySwing;
import org.mmmr.services.swing.common.FancySwing.MoveMouseListener;
import org.mmmr.services.swing.common.RoundedPanel;

public class HDFontWindow extends JFrame {
    private static final long serialVersionUID = -8166133499677459166L;

    private static Object getFieldValue(Object object, Class<?> clazz, String fieldname) throws Exception {
        Field field = clazz.getDeclaredField(fieldname);
        field.setAccessible(true);

        return field.get(object);
    }

    private static Object getFieldValue(Object object, String fieldname) throws Exception {
        Field field = object.getClass().getDeclaredField(fieldname);
        field.setAccessible(true);

        return field.get(object);
    }

    public static void main(String[] args) {
        try {
            new HDFontWindow(NiceFont.prepareFont(new Config()));
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private final Config cfg;

    private JLabel preview;

    public HDFontWindow(Config cfg) {
        this.cfg = cfg;
        this.setIconImage(cfg.getIcon().getImage());
        this.setTitle(cfg.getTitle());
        this.setUndecorated(true);
        final RoundedPanel mainpanel = new RoundedPanel(new BorderLayout());
        mainpanel.setBorder(BorderFactory.createEmptyBorder(20, 40, 20, 40));
        mainpanel.getDelegate().setShady(false);
        new MoveMouseListener(mainpanel);
        this.getContentPane().add(mainpanel, BorderLayout.CENTER);
        JLabel label = new JLabel(this.getTitle());
        label.setHorizontalAlignment(SwingConstants.CENTER);
        label.setFont(cfg.getFontTitle());
        mainpanel.add(label, BorderLayout.NORTH);

        Vector<String> options = new Vector<String>();
        final Map<String, Font> map = new HashMap<String, Font>();

        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        GraphicsDevice gs = ge.getDefaultScreenDevice();
        final GraphicsConfiguration gc = gs.getDefaultConfiguration();
        BufferedImage bimage = gc.createCompatibleImage(1, 1, Transparency.OPAQUE);
        Graphics2D g2d = bimage.createGraphics();

        for (Font font : ge.getAllFonts()) {
            try {
                font = font.deriveFont(Font.PLAIN, 12f);
                @SuppressWarnings("unused")
                String family = font.getFamily();
                font.canDisplay(' ');
                g2d.getFontMetrics(font);
                @SuppressWarnings({ "restriction", "unused" })
                String path = (String) HDFontWindow.getFieldValue(
                        HDFontWindow.getFieldValue(HDFontWindow.getFieldValue(font, "font2DHandle"), "font2D"), sun.font.PhysicalFont.class, //$NON-NLS-1$ //$NON-NLS-2$
                        "platName"); //$NON-NLS-1$
                String full = font.getFontName();
                System.out.println(full);
                options.add(full);
                map.put(full, font);
            } catch (Exception ex) {
                //
            }
        }

        final DefaultComboBoxModel model = new DefaultComboBoxModel(options);
        final JComboBox combo = new JComboBox(model);

        JPanel actions = new JPanel(new GridLayout(1, -1));
        actions.add(combo);
        mainpanel.add(actions, BorderLayout.SOUTH);

        BufferedImage bi = new BufferedImage(8 * 128, 6 * 128, BufferedImage.TYPE_INT_ARGB);
        this.preview = new JLabel(new ImageIcon(bi));
        mainpanel.add(this.preview, BorderLayout.CENTER);

        combo.setSelectedItem("DejaVu Sans Mono"); //$NON-NLS-1$

        combo.addMouseWheelListener(new MouseWheelListener() {
            @Override
            public void mouseWheelMoved(MouseWheelEvent e) {
                if (e.getWheelRotation() < 0) {
                    int newIndex = combo.getSelectedIndex() - 1;
                    if (newIndex >= 0) {
                        combo.setSelectedIndex(newIndex);
                    }
                } else {
                    int newIndex = combo.getSelectedIndex() + 1;
                    if (newIndex < model.getSize()) {
                        combo.setSelectedIndex(newIndex);
                    }
                }
            }
        });

        combo.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(final ItemEvent e) {
                if (e.getStateChange() == ItemEvent.SELECTED) {
                    SwingUtilities.invokeLater(new Runnable() {
                        @Override
                        public void run() {
                            Font font = map.get(e.getItem());
                            BufferedImage prv = NiceFont.hqFontFile(gc, true, HDFontWindow.this.cfg, 8, font);
                            BufferedImage prv2 = new BufferedImage(8 * 128, 6 * 128, BufferedImage.TYPE_INT_ARGB);
                            Graphics2D _g2d = prv2.createGraphics();
                            _g2d.drawImage(prv, null, 0, -2 * 128);
                            _g2d.dispose();
                            mainpanel.remove(HDFontWindow.this.preview);
                            HDFontWindow.this.preview = new JLabel(new ImageIcon(prv2));
                            mainpanel.add(HDFontWindow.this.preview, BorderLayout.CENTER);
                            mainpanel.revalidate();
                        }
                    });
                }
            }
        });

        JButton quit = new JButton(Messages.getString("HDFont.do_not_make_changes")); //$NON-NLS-1$
        quit.setFont(cfg.getFontLarge());
        quit.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                HDFontWindow.this.dispose();
            }
        });
        actions.add(quit);

        JButton choose = new JButton(Messages.getString("HDFont.choose_font")); //$NON-NLS-1$
        choose.setFont(cfg.getFontLarge());
        choose.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                HDFontWindow.this.dispose();
            }
        });
        actions.add(choose);

        this.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

        this.setUndecorated(true);
        FancySwing.translucent(this);
        this.pack();
        FancySwing.rounded(this);
        this.setResizable(false);
    }
}
