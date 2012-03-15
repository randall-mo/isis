package org.apache.isis.extensions.jpa.metamodel.facets.object.namedquery;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.hamcrest.Description;
import org.jmock.Expectations;
import org.jmock.api.Action;
import org.jmock.api.Invocation;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import org.apache.isis.core.commons.matchers.IsisMatchers;
import org.apache.isis.core.metamodel.facets.FacetFactory;
import org.apache.isis.core.progmodel.facets.AbstractFacetFactoryJUnit4TestCase;


public class GivenJpaNamedQueryAnnotationFacetFactoryWhenHasAnnotationsTest
        extends AbstractFacetFactoryJUnit4TestCase {

    private JpaNamedQueryAnnotationFacetFactory facetFactory;


    /**
     * @see #expectJpaNamedQueryFacet()
     * @see #expectJpaNamedQueriesFacet()
     */
    private JpaNamedQueryFacet facet;


    @Before
    public void setUp() throws Exception {
        facetFactory = new JpaNamedQueryAnnotationFacetFactory();
    }

    @After
    public void tearDown() throws Exception {
        facetFactory = null;
    }

    @Test
    public void testNamedQueryAnnotationPickedUpOnClass() {
        expectJpaNamedQueryFacet();

        facetFactory.process(new FacetFactory.ProcessClassContext(SimpleObjectWithNamedQuery.class, methodRemover, mockObjSpec));
    }

    @Test
    public void testNamedQueriesAnnotationPickedUpOnClass() {
        expectJpaNamedQueriesFacet();

        facetFactory.process(new FacetFactory.ProcessClassContext(SimpleObjectWithNamedQueries.class, methodRemover, mockObjSpec));
    }

    @Test
    public void testNamedQueryAnnotationValueType() {
        expectJpaNamedQueryFacet();

        facetFactory.process(new FacetFactory.ProcessClassContext(SimpleObjectWithNamedQuery.class, methodRemover, mockObjSpec));

        final List<NamedQuery> namedQueries = facet.getNamedQueries();
        assertEquals(1, namedQueries.size());
        final NamedQuery namedQuery = namedQueries.get(0);
        assertEquals("searchById", namedQuery.getName());
        assertEquals("from SimpleObjectWithNameQuery where id=?",
                namedQuery.getQuery());
    }

    @Test
    public void testNamedQueriesAnnotationValueType() {
        expectJpaNamedQueriesFacet();

        facetFactory.process(new FacetFactory.ProcessClassContext(SimpleObjectWithNamedQueries.class, methodRemover, mockObjSpec));

        final List<NamedQuery> namedQueries = facet.getNamedQueries();
        assertEquals(2, namedQueries.size());
        final NamedQuery namedQuery0 = namedQueries.get(0);
        assertEquals("searchById", namedQuery0.getName());
        assertEquals("from SimpleObjectWithNameQuery where id=?",
                namedQuery0.getQuery());
        final NamedQuery namedQuery1 = namedQueries.get(1);
        assertEquals("searchByName", namedQuery1.getName());
        assertEquals("from SimpleObjectWithNameQuery where name=?",
                namedQuery1.getQuery());
    }

    @Test
    public void testWhenAnnotatedWithBothNamedQueriesAndNamedQueryWillIgnoreTheNamedQuery() {
        expectJpaNamedQueriesFacet();

        facetFactory.process(new FacetFactory.ProcessClassContext(SimpleObjectWithNamedQueriesAndNamedQuery.class, methodRemover, mockObjSpec));

        final List<NamedQuery> namedQueries = facet.getNamedQueries();
        assertEquals(2, namedQueries.size());
        final NamedQuery namedQuery0 = namedQueries.get(0);
        assertEquals("searchById", namedQuery0.getName());
        assertEquals("from SimpleObjectWithNameQuery where id=?",
                namedQuery0.getQuery());
        final NamedQuery namedQuery1 = namedQueries.get(1);
        assertEquals("searchByName", namedQuery1.getName());
        assertEquals("from SimpleObjectWithNameQuery where name=?",
                namedQuery1.getQuery());
    }

    @Test
    public void testNoMethodsRemovedForNamedQueryAnnotation() {
        expectJpaNamedQueryFacet();
        expectMethodRemoverNeverCalled();

        facetFactory.process(new FacetFactory.ProcessClassContext(SimpleObjectWithNamedQuery.class, methodRemover, mockObjSpec));
    }

    @Test
    public void testNoMethodsRemovedForNamedQueriesAnnotation() {
        expectJpaNamedQueriesFacet();
        expectMethodRemoverNeverCalled();

        facetFactory.process(new FacetFactory.ProcessClassContext(SimpleObjectWithNamedQueries.class, methodRemover, mockObjSpec));
    }


    // ////////////////////////////////////////////////////////////////////////

    private final class StoreFacetAction implements Action {
        public void describeTo(final Description arg0) {}

        public Object invoke(final Invocation invocation) throws Throwable {
            facet = (JpaNamedQueryFacet) invocation.getParameter(0);
            return null;
        }
    }

    private void expectJpaNamedQueryFacet() {
        context.checking(new Expectations() {
            {
                one(mockObjSpec)
                        .addFacet(
                        with(IsisMatchers
                        .anInstanceOf(JpaNamedQueryFacetAnnotation.class)));
                will(new StoreFacetAction());
            }
        });
    }

    private void expectJpaNamedQueriesFacet() {
        context.checking(new Expectations() {
            {
                one(mockObjSpec)
                        .addFacet(
                        with(IsisMatchers
                        .anInstanceOf(JpaNamedQueriesFacetAnnotation.class)));
                will(new StoreFacetAction());
            }
        });
    }

    private void expectMethodRemoverNeverCalled() {
        context.checking(new Expectations() {
            {
                never(methodRemover);
            }
        });
    }


}

// Copyright (c) Naked Objects Group Ltd.