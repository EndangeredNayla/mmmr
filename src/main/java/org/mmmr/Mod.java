package org.mmmr;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.persistence.Version;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import org.hibernate.annotations.Cascade;

/**
 * @author Jurgen
 */
@XmlRootElement(name = "mod")
@Entity
@Table(uniqueConstraints = { @UniqueConstraint(name = "mod_name_version", columnNames = { "name", "version" }) })
public class Mod implements PersistentObject {
    private String archive;

    @OneToMany(cascade = { CascadeType.PERSIST, CascadeType.REMOVE }, mappedBy = "mod")
    @Cascade({ org.hibernate.annotations.CascadeType.SAVE_UPDATE })
    private List<Dependency> dependencies;

    private String description;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private Date installationDate;

    @ManyToOne(fetch = FetchType.EAGER, cascade = { CascadeType.PERSIST, CascadeType.REMOVE })
    @Cascade({ org.hibernate.annotations.CascadeType.SAVE_UPDATE })
    private ModPack modPack;

    @Column(nullable = false)
    private String name;

    private String resourceCheck;

    @OneToMany(cascade = { CascadeType.PERSIST, CascadeType.REMOVE }, mappedBy = "mod")
    @Cascade({ org.hibernate.annotations.CascadeType.SAVE_UPDATE })
    private List<Resource> resources;

    private String url;

    @Version
    private Integer ver;

    @Column(nullable = false)
    private String version;

    public Mod() {
	super();
    }

    public Mod(String archive) {
	this();
	this.archive = archive;
    }

    public Mod(String name, String version) {
	this();
	this.name = name;
	this.version = version;
    }

    public Mod(String name, String version, String url) {
	this();
	this.name = name;
	this.version = version;
	this.url = url;
    }

    public Mod(String name, String version, String url, String resourceCheck) {
	this();
	this.name = name;
	this.version = version;
	this.url = url;
	this.resourceCheck = resourceCheck;
    }

    public void addDepencency(Dependency dependency) {
	if (getDependencies() == null)
	    dependencies = new ArrayList<Dependency>();
	getDependencies().add(dependency);
	dependency.setMod(this);
    }

    public void addResource(Resource resource) {
	if (getResources() == null)
	    resources = new ArrayList<Resource>();
	getResources().add(resource);
	resource.setMod(this);
    }

    @Override
    public boolean equals(Object obj) {
	if (this == obj)
	    return true;
	if (obj == null)
	    return false;
	if (getClass() != obj.getClass())
	    return false;
	Mod other = (Mod) obj;
	if (this.getArchive() == null) {
	    if (other.getArchive() != null)
		return false;
	} else if (!this.getArchive().equals(other.getArchive()))
	    return false;
	if (this.getName() == null) {
	    if (other.getName() != null)
		return false;
	} else if (!this.getName().equals(other.getName()))
	    return false;
	if (this.getVersion() == null) {
	    if (other.getVersion() != null)
		return false;
	} else if (!this.getVersion().equals(other.getVersion()))
	    return false;
	return true;
    }

    @XmlAttribute
    public String getArchive() {
	return this.archive;
    }

    @XmlElementWrapper
    @XmlElementRef
    public List<Dependency> getDependencies() {
	return this.dependencies;
    }

    @XmlAttribute
    public String getDescription() {
	return this.description;
    }

    @XmlTransient
    public Long getId() {
	return this.id;
    }

    @XmlTransient
    public Date getInstallationDate() {
	return this.installationDate;
    }

    @XmlTransient
    public ModPack getModPack() {
	return modPack;
    }

    @XmlAttribute(required = true)
    public String getName() {
	return this.name;
    }

    @XmlElement(name = "resourcecheck")
    public String getResourceCheck() {
	return resourceCheck;
    }

    @XmlElementWrapper
    @XmlElementRef
    public List<Resource> getResources() {
	return this.resources;
    }

    @XmlAttribute
    public String getUrl() {
	return url;
    }

    @XmlTransient
    public Integer getVer() {
	return this.ver;
    }

    @XmlAttribute(required = true)
    public String getVersion() {
	return this.version;
    }

    @Override
    public int hashCode() {
	final int prime = 31;
	int result = 1;
	result = prime * result + ((this.getArchive() == null) ? 0 : this.getArchive().hashCode());
	result = prime * result + ((this.getName() == null) ? 0 : this.getName().hashCode());
	result = prime * result + ((this.getVersion() == null) ? 0 : this.getVersion().hashCode());
	return result;
    }

    public boolean isInstalled() {
	return getInstallationDate() != null;
    }

    public void setArchive(String archive) {
	this.archive = archive;
    }

    public void setDependencies(List<Dependency> dependencies) {
	this.dependencies = dependencies;
    }

    public void setDescription(String description) {
	this.description = description;
    }

    protected void setId(Long id) {
	this.id = id;
    }

    public void setInstallationDate(Date installationDate) {
	this.installationDate = installationDate;
    }

    protected void setModPack(ModPack modPack) {
	this.modPack = modPack;
    }

    public void setName(String name) {
	this.name = name;
    }

    public void setResourceCheck(String resourceCheck) {
	this.resourceCheck = resourceCheck;
    }

    public void setResources(List<Resource> resources) {
	this.resources = resources;
    }

    public void setUrl(String url) {
	this.url = url;
    }

    protected void setVer(Integer ver) {
	this.ver = ver;
    }

    public void setVersion(String version) {
	this.version = version;
    }

    @Override
    public String toString() {
	return "Mod [name=" + getName() + ", version=" + getVersion() + ", description=" + getDescription() + ", archive=" + getArchive() + ", url=" + getUrl()
		+ ", installationDate=" + getInstallationDate() + ", resources=" + getResources() + ", dependencies=" + getDependencies() + "]";
    }
}
