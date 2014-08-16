/**
 * 
 */
package org.minnal.instrument.entity;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.testng.Assert.assertEquals;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;

import org.activejpa.entity.Model;
import org.minnal.instrument.NamingStrategy;
import org.minnal.instrument.UnderscoreNamingStrategy;
import org.minnal.instrument.entity.EntityNode.EntityNodePath;
import org.minnal.instrument.entity.metadata.EntityMetaDataProvider;
import org.minnal.utils.Node.PathVisitor;
import org.testng.annotations.Test;

/**
 * @author ganeshs
 *
 */
public class EntityNodeTest {
	
	private EntityNode entityNode;
	
	private NamingStrategy namingStrategy = new UnderscoreNamingStrategy();

	@Test
	public void shouldConstructEntityTree() {
		entityNode = new EntityNode(CompositeModel.class, namingStrategy);
		entityNode.construct();
		assertEquals(entityNode.getChildren().size(), 2);
	}
	
	@Test
	public void shouldPopulateEntityNodeName() {
		entityNode = new EntityNode(CompositeModel.class, namingStrategy);
		assertEquals(entityNode.getName(), "compositeModel");
	}
	
	@Test
	public void shouldPopulateEntityResourceName() {
		entityNode = new EntityNode(CompositeModel.class, namingStrategy);
		assertEquals(entityNode.getResourceName(), "composite_models");
	}
	
	@Test
	public void shouldPopulateEntityMetaData() {
		entityNode = new EntityNode(Parent.class, namingStrategy);
		assertEquals(entityNode.getEntityMetaData(), EntityMetaDataProvider.instance().getEntityMetaData(Parent.class));
	}
	
	@Test
	public void shouldPopulateEntityMetaDataWithoutLooping() {
		entityNode = new EntityNode(CompositeModel.class, namingStrategy);
		entityNode.construct();
		assertEquals(entityNode.getEntityMetaData(), EntityMetaDataProvider.instance().getEntityMetaData(CompositeModel.class));
	}
	
	@Test
	public void shouldPopulateEntityMetaDataWithBidirectionalAssociation() {
		entityNode = new EntityNode(Employee.class, namingStrategy);
		entityNode.construct();
		assertEquals(entityNode.getEntityMetaData(), EntityMetaDataProvider.instance().getEntityMetaData(Employee.class));
	}
	
	@Test
	public void shouldCreateEntityNodePath() {
		entityNode = new EntityNode(Parent.class, namingStrategy);
		entityNode.construct();
		List<EntityNode> path = Arrays.asList(entityNode, entityNode.getChildren().iterator().next());
		assertEquals(entityNode.createNodePath(path), entityNode.new EntityNodePath(path));
	}
	
	@Test
	public void shouldTraverseEntityTree() {
		entityNode = new EntityNode(Parent.class, namingStrategy);
		entityNode.construct();
		PathVisitor<EntityNodePath, EntityNode> visitor = mock(PathVisitor.class);
		entityNode.traverse(visitor);
		verify(visitor).visit(entityNode.createNodePath(Arrays.asList(entityNode)));
		verify(visitor).visit(entityNode.createNodePath(Arrays.asList(entityNode, entityNode.getChildren().iterator().next())));
	}
	
	@Test
	public void shouldGetEntityNodePathForPathString() {
		entityNode = new EntityNode(Parent.class, namingStrategy);
		entityNode.construct();
		EntityNodePath path = entityNode.getEntityNodePath("children");
		assertEquals(path.getBulkPath(), "/parents/{parent_id}/children");
	}
	
	@Entity
	private class CompositeModel extends Model {
		private Long id;
		private String code;
		@OneToMany
		private Set<CompositeModel> children;
		@OneToMany
		private Set<CompositeModel> siblings;
		@Override
		public Serializable getId() {
			return null;
		}
	}
	
	private class Employee extends Model {
		@Id
		private Long id;
		@ManyToMany
		private Set<Department> departments;
		@Override
		public Serializable getId() {
			return id;
		}
	}
	
	private class Department extends Model {
		@Id
		private Long id;
		@ManyToMany
		private Set<Employee> employees;
		@Override
		public Serializable getId() {
			return id;
		}
	}
	
	@Entity
	private class Parent extends Model {
		@Id
		@Searchable
		private Long id;
		@EntityKey
		@Searchable
		private String code;
		@OneToMany
		private Set<Child> children;
		@Override
		public Serializable getId() {
			return id;
		}
	}
	
	@Entity
	private class Child extends Model {
		@Id
		private Long id;
		@EntityKey
		@Searchable
		private String code;
		@OneToMany
		public Serializable getId() {
			return null;
		}
	}
}
