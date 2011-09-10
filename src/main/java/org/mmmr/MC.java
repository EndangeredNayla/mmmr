package org.mmmr;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.persistence.Version;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import org.apache.commons.lang.builder.CompareToBuilder;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.hibernate.annotations.Cascade;

/**
 * @author Jurgen
 */
@XmlRootElement(name = "mc")
@Entity
@Table(uniqueConstraints = { @UniqueConstraint(name = "mc_version", columnNames = { "version" }) })
public class MC implements Comparable<MC>, PersistentObject {
    @OneToMany(cascade = { CascadeType.PERSIST, CascadeType.REMOVE }, mappedBy = "mc")
    @Cascade({ org.hibernate.annotations.CascadeType.SAVE_UPDATE })
    private List<MCFile> files;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Version
    private Integer ver;

    private String version;

    public MC() {
	super();
    }

    public MC(String version) {
	this();
	setVersion(version);
    }

    public void addFile(MCFile file) {
	if (getFiles() == null)
	    files = new ArrayList<MCFile>();
	getFiles().add(file);
	file.setMc(this);
    }

    public int compareTo(final MC other) {
	return new CompareToBuilder().append(version, other.version).toComparison();
    }

    @Override
    public boolean equals(final Object other) {
	if (!(other instanceof MC))
	    return false;
	MC castOther = (MC) other;
	return new EqualsBuilder().append(version, castOther.version).isEquals();
    }

    @XmlTransient
    public List<MCFile> getFiles() {
	return this.files;
    }

    @XmlTransient
    public Long getId() {
	return this.id;
    }

    @XmlTransient
    public Integer getVer() {
	return this.ver;
    }

    @XmlAttribute
    public String getVersion() {
	return version;
    }

    @Override
    public int hashCode() {
	return new HashCodeBuilder().append(version).toHashCode();
    }

    public void setFiles(List<MCFile> files) {
	this.files = files;
    }

    protected void setId(Long id) {
	this.id = id;
    }

    protected void setVer(Integer ver) {
	this.ver = ver;
    }

    public void setVersion(String version) {
	this.version = version;
    }

    @Override
    public String toString() {
	return new ToStringBuilder(this).appendSuper(super.toString()).append("version", version).toString();
    }
}
