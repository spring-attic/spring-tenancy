package org.springframework.tenancy.datasource;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class DomainObject {
	private Long id;
	private String string;

	@Id
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	@Override
	public int hashCode() {
		if (id != null) {
			return id.hashCode();
		}
		return super.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		DomainObject other = (DomainObject) obj;
		if (id != null && id.equals(other.id)) {
			return true;
		}
		return false;
	}

	@Basic(optional = true)
	@Column(nullable = true)
	public String getString() {
		return string;
	}

	public void setString(String string) {
		this.string = string;
	}

}
