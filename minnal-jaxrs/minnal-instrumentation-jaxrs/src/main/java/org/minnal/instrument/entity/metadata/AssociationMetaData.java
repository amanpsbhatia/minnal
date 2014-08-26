/**
 * 
 */
package org.minnal.instrument.entity.metadata;

import org.minnal.instrument.metadata.MetaData;

/**
 * @author ganeshs
 *
 */
public class AssociationMetaData extends MetaData {

	private Class<?> type;
	
	private boolean entity;

	/**
	 * @param name
	 * @param type
	 * @param entity
	 */
	public AssociationMetaData(String name, Class<?> type, boolean entity) {
		super(name);
		this.type = type;
		this.entity = entity;
	}

	/**
	 * @return the type
	 */
	public Class<?> getType() {
		return type;
	}

	/**
	 * @param type the type to set
	 */
	public void setType(Class<?> type) {
		this.type = type;
	}

	/**
	 * @return the entity
	 */
	public boolean isEntity() {
		return entity;
	}

	/**
	 * @param entity the entity to set
	 */
	public void setEntity(boolean entity) {
		this.entity = entity;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (entity ? 1231 : 1237);
		result = prime * result + ((getName() == null) ? 0 : getName().hashCode());
		result = prime * result + ((type == null) ? 0 : type.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		AssociationMetaData other = (AssociationMetaData) obj;
		if (entity != other.entity)
			return false;
		if (getName() == null) {
			if (other.getName() != null)
				return false;
		} else if (!getName().equals(other.getName()))
			return false;
		if (type == null) {
			if (other.type != null)
				return false;
		} else if (!type.equals(other.type))
			return false;
		return true;
	}
	
}
