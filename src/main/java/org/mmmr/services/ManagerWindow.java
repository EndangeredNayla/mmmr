package org.mmmr.services;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import org.apache.log4j.Appender;
import org.apache.log4j.Level;
import org.apache.log4j.varia.LevelRangeFilter;
import org.mmmr.Mod;

/**
 * @author Jurgen
 */
public class ManagerWindow extends JFrame {
    private class ModOption {
	private final Mod mod;

	public ModOption(Mod mod) {
	    super();
	    this.mod = mod;
	}

	public Mod getMod() {
	    return mod;
	}

	@Override
	public String toString() {
	    StringBuilder sb = new StringBuilder(mod.getName()).append(" v").append(mod.getVersion());

	    if (mod.isInstalled())
		sb.append(" [installed]");

	    File file = new File(cfg.getMods(), mod.getArchive());
	    if (!file.exists()) {
		sb.append(" [archive not found]");
	    }

	    return sb.toString();
	}
    }

    private static final long serialVersionUID = -2874170242621940902L;

    private Config cfg;

    private InstallationService iserv = new InstallationService();

    public ManagerWindow(Config cfg) {
	this.cfg = cfg;
	setTitle("Minecraft Mod Manager Reloaded 1.0b For Minecraft 1.7.3b");
	setUndecorated(true);
	Container cp = getContentPane();
	JPanel panel = new JPanel(new GridLayout(-1, 1));
	panel.setBorder(BorderFactory.createEmptyBorder(25, 50, 25, 50));
	cp.add(panel, BorderLayout.CENTER);
	cp = panel;
	JLabel label = new JLabel(getTitle());
	label.setHorizontalAlignment(SwingConstants.CENTER);
	label.setFont(cfg.getFont18().deriveFont(20f).deriveFont(Font.BOLD));
	cp.add(label);
	addActions(cp);
	JButton quit = new JButton("Get me out of here :(");
	quit.setFont(cfg.getFont().deriveFont(14f).deriveFont(Font.BOLD));
	quit.addActionListener(new ActionListener() {
	    @Override
	    public void actionPerformed(ActionEvent e) {
		dispose();
	    }
	});
	cp.add(quit);
	setLocationRelativeTo(null);
	setResizable(false);
	FancySwing.translucent(this);
	pack();
	setSize(800, getHeight());
	setLocationRelativeTo(null);
	FancySwing.rounded(this);
	setVisible(true);
    }

    private void addActions(Container cp) {
	{
	    JButton comp = new JButton("Install OptiFine (performance mod & HD texture enabler)");
	    comp.setFont(cfg.getFont18());
	    comp.addActionListener(new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent e) {
		    performanceMod();
		}
	    });
	    cp.add(comp);
	}
	{
	    JButton comp = new JButton("Change startup configuration (performance related)");
	    comp.setFont(cfg.getFont18());
	    comp.addActionListener(new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent e) {
		    // TODO
		}
	    });
	    cp.add(comp);
	}
	{
	    JButton comp = new JButton("Install mods");
	    comp.setFont(cfg.getFont18());
	    comp.addActionListener(new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent e) {
		    installMods();
		}
	    });
	    cp.add(comp);
	}
	{
	    JButton comp = new JButton("Uninstall mods");
	    comp.setFont(cfg.getFont18());
	    comp.addActionListener(new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent e) {
		    // TODO
		}
	    });
	    cp.add(comp);
	}
	{
	    JButton comp = new JButton("Change sex");
	    comp.setFont(cfg.getFont18());
	    comp.addActionListener(new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent e) {
		    // TODO
		}
	    });
	    cp.add(comp);
	}
	{
	    JButton comp = new JButton("Change MMMR logging level");
	    comp.setFont(cfg.getFont18());
	    comp.addActionListener(new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent e) {
		    try {
			Level[] levels = { Level.TRACE, Level.DEBUG, Level.WARN, Level.ERROR, Level.FATAL, Level.OFF };
			Level level = Level.class.cast(JOptionPane.showInputDialog(null, "Choose logging level", "Logging", JOptionPane.QUESTION_MESSAGE, null, levels,
				org.apache.log4j.Logger.getRootLogger().getLevel()));
			if (level != null) {
			    cfg.setProperty("logging.level", String.valueOf(level));
			    org.apache.log4j.Logger.getRootLogger().setLevel(level);
			    Enumeration<?> allAppenders = org.apache.log4j.Logger.getRootLogger().getAllAppenders();
			    while (allAppenders.hasMoreElements()) {
				Appender appender = Appender.class.cast(allAppenders.nextElement());
				LevelRangeFilter filter = LevelRangeFilter.class.cast(appender.getFilter());
				filter.setLevelMin(level);
			    }
			}
		    } catch (Exception e2) {
			e2.printStackTrace();
		    }
		}
	    });
	    cp.add(comp);
	}
    }

    private void installMods() {
	try {
	    File[] modxmls = cfg.getMods().listFiles(new FilenameFilter() {
		@Override
		public boolean accept(File dir, String name) {
		    if (!name.endsWith(".xml"))
			return false;
		    if (name.toLowerCase().contains("optifine"))
			return false;
		    if (name.toLowerCase().contains("yogbox"))
			return false;
		    return true;
		}
	    });
	    List<ModOption> options = new ArrayList<ModOption>();
	    for (File modxml : modxmls) {
		Mod availablemod = cfg.getXml().load(new FileInputStream(modxml), Mod.class);
		Mod installedmod = cfg.getDb().get(new Mod(availablemod.getName(), availablemod.getVersion()));
		if (installedmod != null && installedmod.isInstalled())
		    continue;
		options.add(new ModOption(availablemod));
	    }
	    ModOption selected = ModOption.class.cast(JOptionPane.showInputDialog(null, "Select a version", "Select a version", JOptionPane.QUESTION_MESSAGE, null,
		    options.toArray(), options.get(0)));
	    if (selected != null) {
		iserv.installMod(cfg.getDb(), selected.getMod(), cfg.getMods(), cfg.getTmp(), cfg.getMcBaseFolder());
	    }
	} catch (Exception e) {
	    e.printStackTrace();
	}
    }

    private void performanceMod() {
	try {
	    File[] modxmls = cfg.getMods().listFiles(new FilenameFilter() {
		@Override
		public boolean accept(File dir, String name) {
		    return name.toLowerCase().contains("optifine") && name.endsWith(".xml");
		}
	    });
	    List<ModOption> options = new ArrayList<ModOption>();
	    Mod installed = null;
	    ModOption installedOption = null;
	    for (File modxml : modxmls) {
		Mod availablemod = cfg.getXml().load(new FileInputStream(modxml), Mod.class);
		Mod installedmod = cfg.getDb().get(new Mod(availablemod.getName(), availablemod.getVersion()));
		if (installedmod != null && installedmod.isInstalled())
		    installed = installedmod;
		Mod mod = installedmod != null ? installedmod : availablemod;
		ModOption modoption = new ModOption(mod);
		if (installedmod != null && installedmod.isInstalled())
		    installedOption = modoption;
		options.add(modoption);
	    }
	    ModOption[] selectionValues = options.toArray(new ModOption[options.size()]);
	    ModOption selected = installedOption == null ? selectionValues[0] : installedOption;
	    selected = ModOption.class.cast(JOptionPane
		    .showInputDialog(null, "Select a version", "Select a version", JOptionPane.QUESTION_MESSAGE, null, selectionValues, selected));
	    if (selected != null) {
		Mod mod = ModOption.class.cast(selected).getMod();
		if (installed != null) {
		    if (mod.equals(installed)) {
			// already installed, nothing to do
		    } else {
			iserv.uninstallMod(cfg.getDb(), installed, cfg.getMods(), cfg.getTmp(), cfg.getMcBaseFolder());
			iserv.installMod(cfg.getDb(), mod, cfg.getMods(), cfg.getTmp(), cfg.getMcBaseFolder());
		    }
		} else {
		    iserv.installMod(cfg.getDb(), mod, cfg.getMods(), cfg.getTmp(), cfg.getMcBaseFolder());
		}
	    }
	} catch (Exception e2) {
	    e2.printStackTrace();
	}
    }
}
