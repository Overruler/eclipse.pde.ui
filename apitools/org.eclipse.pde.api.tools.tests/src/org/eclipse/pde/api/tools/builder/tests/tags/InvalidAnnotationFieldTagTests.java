/*******************************************************************************
 * Copyright (c) 2008 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.pde.api.tools.builder.tests.tags;

import junit.framework.Test;

import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jdt.internal.compiler.impl.CompilerOptions;
import org.eclipse.pde.api.tools.builder.tests.ApiProblem;

/**
 * Tests the use of invalid tags in annotation fields and constants
 * 
 * @since 3.4
 */
public class InvalidAnnotationFieldTagTests extends InvalidFieldTagTests {

	/**
	 * Constructor
	 * @param name
	 */
	public InvalidAnnotationFieldTagTests(String name) {
		super(name);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.pde.api.tools.builder.tests.tags.InvalidFieldTagTests#getTestSourcePath()
	 */
	protected IPath getTestSourcePath() {
		return super.getTestSourcePath().append("annotation");
	}
	
	/**
	 * @return the test for this class
	 */
	public static Test suite() {
		return buildTestSuite(InvalidAnnotationFieldTagTests.class);
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.pde.api.tools.builder.tests.ApiBuilderTest#getTestCompliance()
	 */
	protected String getTestCompliance() {
		return CompilerOptions.VERSION_1_5;
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.pde.api.tools.builder.tests.ApiBuilderTest#assertProblems(org.eclipse.pde.api.tools.builder.tests.ApiProblem[])
	 */
	protected void assertProblems(ApiProblem[] problems) {
		String message = null;
		for(int i = 0; i < problems.length; i++) {
			message = problems[i].getMessage();
			assertTrue("The problem message is not correct: "+message, message.endsWith("an annotation field"));
		}
	}
	
	/**
	 * Tests the unsupported @noextend tag on an annotation field
	 * using an incremental build
	 */
	public void testInvalidAnnotationFieldTag1I() {
		setExpectedProblemIds(getDefaultProblemSet(1));
		deployTagTest(TESTING_PACKAGE, "test1", true, IncrementalProjectBuilder.INCREMENTAL_BUILD, true);
	}
	
	/**
	 * Tests the unsupported @noextend tag on an annotation field
	 * using a full build
	 */
	public void testInvalidAnnotationFieldTag1F() {
		setExpectedProblemIds(getDefaultProblemSet(1));
		deployTagTest(TESTING_PACKAGE, "test1", true, IncrementalProjectBuilder.FULL_BUILD, true);
	}
	
	/**
	 * Tests the unsupported @noextend tag on an outer annotation field
	 * using an incremental build
	 */
	public void testInvalidAnnotationFieldTag2I() {
		setExpectedProblemIds(getDefaultProblemSet(1));
		deployTagTest(TESTING_PACKAGE, "test2", true, IncrementalProjectBuilder.INCREMENTAL_BUILD, true);
	}
	
	/**
	 * Tests the unsupported @noextend tag on an outer annotation field
	 * using a full build
	 */
	public void testInvalidAnnotationFieldTag2F() {
		setExpectedProblemIds(getDefaultProblemSet(1));
		deployTagTest(TESTING_PACKAGE, "test2", true, IncrementalProjectBuilder.FULL_BUILD, true);
	}
	
	/**
	 * Tests the unsupported @noextend tag on an inner annotation field
	 * using an incremental build
	 */
	public void testInvalidAnnotationFieldTag3I() {
		setExpectedProblemIds(getDefaultProblemSet(1));
		deployTagTest(TESTING_PACKAGE, "test3", true, IncrementalProjectBuilder.INCREMENTAL_BUILD, true);
	}
	
	/**
	 * Tests the unsupported @noextend tag on an inner annotation field
	 * using a full build
	 */
	public void testInvalidAnnotationFieldTag3F() {
		setExpectedProblemIds(getDefaultProblemSet(1));
		deployTagTest(TESTING_PACKAGE, "test3", true, IncrementalProjectBuilder.FULL_BUILD, true);
	}
	
	/**
	 * Tests the unsupported @noextend tag on a variety of inner / outer annotation fields
	 * using an incremental build
	 */
	public void testInvalidAnnotationFieldTag4I() {
		setExpectedProblemIds(getDefaultProblemSet(3));
		deployTagTest(TESTING_PACKAGE, "test4", true, IncrementalProjectBuilder.INCREMENTAL_BUILD, true);
	}
	
	/**
	 * Tests the unsupported @noextend tag on a variety of inner / outer annotation fields
	 * using a full build
	 */
	public void testInvalidAnnotationFieldTag4F() {
		setExpectedProblemIds(getDefaultProblemSet(3));
		deployTagTest(TESTING_PACKAGE, "test4", true, IncrementalProjectBuilder.FULL_BUILD, true);
	}
	
	/**
	 * Tests the unsupported @noextend tag on an annotation field in the default package
	 * using an incremental build
	 */
	public void testInvalidAnnotationFieldTag5I() {
		setExpectedProblemIds(getDefaultProblemSet(1));
		deployTagTest("", "test5", true, IncrementalProjectBuilder.INCREMENTAL_BUILD, true);
	}
	
	/**
	 * Tests the unsupported @noextend tag on an annotation field in the default package
	 * using a full build
	 */
	public void testInvalidAnnotationFieldTag5F() {
		setExpectedProblemIds(getDefaultProblemSet(1));
		deployTagTest("", "test5", true, IncrementalProjectBuilder.FULL_BUILD, true);
	}
	
	/**
	 * Tests the unsupported @noinstantiate tag on an annotation field
	 * using an incremental build
	 */
	public void testInvalidAnnotationFieldTag6I() {
		setExpectedProblemIds(getDefaultProblemSet(1));
		deployTagTest(TESTING_PACKAGE, "test6", true, IncrementalProjectBuilder.INCREMENTAL_BUILD, true);
	}
	
	/**
	 * Tests the unsupported @noinstantiate tag on an annotation field
	 * using a full build
	 */
	public void testInvalidAnnotationFieldTag6F() {
		setExpectedProblemIds(getDefaultProblemSet(1));
		deployTagTest(TESTING_PACKAGE, "test6", true, IncrementalProjectBuilder.FULL_BUILD, true);
	}
	
	/**
	 * Tests the unsupported @noinstantiate tag on an outer annotation field
	 * using an incremental build
	 */
	public void testInvalidAnnotationFieldTag7I() {
		setExpectedProblemIds(getDefaultProblemSet(1));
		deployTagTest(TESTING_PACKAGE, "test7", true, IncrementalProjectBuilder.INCREMENTAL_BUILD, true);
	}
	
	/**
	 * Tests the unsupported @noinstantiate tag on an outer annotation field
	 * using a full build
	 */
	public void testInvalidAnnotationFieldTag7F() {
		setExpectedProblemIds(getDefaultProblemSet(1));
		deployTagTest(TESTING_PACKAGE, "test7", true, IncrementalProjectBuilder.FULL_BUILD, true);
	}
	
	/**
	 * Tests the unsupported @noinstantiate tag on an inner annotation field
	 * using an incremental build
	 */
	public void testInvalidAnnotationFieldTag8I() {
		setExpectedProblemIds(getDefaultProblemSet(1));
		deployTagTest(TESTING_PACKAGE, "test8", true, IncrementalProjectBuilder.INCREMENTAL_BUILD, true);
	}
	
	/**
	 * Tests the unsupported @noinstantiate tag on an inner annotation field
	 * using a full build
	 */
	public void testInvalidAnnotationFieldTag8F() {
		setExpectedProblemIds(getDefaultProblemSet(1));
		deployTagTest(TESTING_PACKAGE, "test8", true, IncrementalProjectBuilder.FULL_BUILD, true);
	}
	
	/**
	 * Tests the unsupported @noinstantiate tag on a variety of inner / outer annotation fields
	 * using an incremental build
	 */
	public void testInvalidAnnotationFieldTag9I() {
		setExpectedProblemIds(getDefaultProblemSet(3));
		deployTagTest(TESTING_PACKAGE, "test9", true, IncrementalProjectBuilder.INCREMENTAL_BUILD, true);
	}
	
	/**
	 * Tests the unsupported @noinstantiate tag on a variety of inner / outer annotation fields
	 * using a full build
	 */
	public void testInvalidAnnotationFieldTag9F() {
		setExpectedProblemIds(getDefaultProblemSet(3));
		deployTagTest(TESTING_PACKAGE, "test9", true, IncrementalProjectBuilder.FULL_BUILD, true);
	}
	
	/**
	 * Tests the unsupported @noinstantiate tag on an annotation field in the default package
	 * using an incremental build
	 */
	public void testInvalidAnnotationFieldTag10I() {
		setExpectedProblemIds(getDefaultProblemSet(1));
		deployTagTest("", "test10", true, IncrementalProjectBuilder.INCREMENTAL_BUILD, true);
	}
	
	/**
	 * Tests the unsupported @noinstantiate tag on an annotation field in the default package
	 * using a full build
	 */
	public void testInvalidAnnotationFieldTag10F() {
		setExpectedProblemIds(getDefaultProblemSet(1));
		deployTagTest("", "test10", true, IncrementalProjectBuilder.FULL_BUILD, true);
	}
	
	/**
	 * Tests the unsupported @noimplement tag on an annotation field
	 * using an incremental build
	 */
	public void testInvalidAnnotationFieldTag11I() {
		setExpectedProblemIds(getDefaultProblemSet(1));
		deployTagTest(TESTING_PACKAGE, "test11", true, IncrementalProjectBuilder.INCREMENTAL_BUILD, true);
	}
	
	/**
	 * Tests the unsupported @noimplement tag on an annotation field
	 * using a full build
	 */
	public void testInvalidAnnotationFieldTag11F() {
		setExpectedProblemIds(getDefaultProblemSet(1));
		deployTagTest(TESTING_PACKAGE, "test11", true, IncrementalProjectBuilder.FULL_BUILD, true);
	}
	
	/**
	 * Tests the unsupported @noimplement tag on an outer annotation field
	 * using an incremental build
	 */
	public void testInvalidAnnotationFieldTag12I() {
		setExpectedProblemIds(getDefaultProblemSet(1));
		deployTagTest(TESTING_PACKAGE, "test12", true, IncrementalProjectBuilder.INCREMENTAL_BUILD, true);
	}
	
	/**
	 * Tests the unsupported @noimplement tag on an outer annotation field
	 * using a full build
	 */
	public void testInvalidAnnotationFieldTag12F() {
		setExpectedProblemIds(getDefaultProblemSet(1));
		deployTagTest(TESTING_PACKAGE, "test12", true, IncrementalProjectBuilder.FULL_BUILD, true);
	}
	
	/**
	 * Tests the unsupported @noimplement tag on an inner annotation field
	 * using an incremental build
	 */
	public void testInvalidAnnotationFieldTag13I() {
		setExpectedProblemIds(getDefaultProblemSet(1));
		deployTagTest(TESTING_PACKAGE, "test13", true, IncrementalProjectBuilder.INCREMENTAL_BUILD, true);
	}
	
	/**
	 * Tests the unsupported @noimplement tag on an inner annotation field
	 * using a full build
	 */
	public void testInvalidAnnotationFieldTag13F() {
		setExpectedProblemIds(getDefaultProblemSet(1));
		deployTagTest(TESTING_PACKAGE, "test13", true, IncrementalProjectBuilder.FULL_BUILD, true);
	}
	
	/**
	 * Tests the unsupported @noimplement tag on a variety of inner / outer annotation fields
	 * using an incremental build
	 */
	public void testInvalidAnnotationFieldTag14I() {
		setExpectedProblemIds(getDefaultProblemSet(3));
		deployTagTest(TESTING_PACKAGE, "test14", true, IncrementalProjectBuilder.INCREMENTAL_BUILD, true);
	}
	
	/**
	 * Tests the unsupported @noimplement tag on a variety of inner / outer annotation fields
	 * using a full build
	 */
	public void testInvalidAnnotationFieldTag14F() {
		setExpectedProblemIds(getDefaultProblemSet(3));
		deployTagTest(TESTING_PACKAGE, "test14", true, IncrementalProjectBuilder.FULL_BUILD, true);
	}
	
	/**
	 * Tests the unsupported @noimplement tag on an annotation field in the default package
	 * using an incremental build
	 */
	public void testInvalidAnnotationFieldTag15I() {
		setExpectedProblemIds(getDefaultProblemSet(1));
		deployTagTest("", "test15", true, IncrementalProjectBuilder.INCREMENTAL_BUILD, true);
	}
	
	/**
	 * Tests the unsupported @noimplement tag on an annotation field in the default package
	 * using a full build
	 */
	public void testInvalidAnnotationFieldTag15F() {
		setExpectedProblemIds(getDefaultProblemSet(1));
		deployTagTest("", "test15", true, IncrementalProjectBuilder.FULL_BUILD, true);
	}
	
	/**
	 * Tests the unsupported @nooverride tag on an annotation field
	 * using an incremental build
	 */
	public void testInvalidAnnotationFieldTag16I() {
		setExpectedProblemIds(getDefaultProblemSet(1));
		deployTagTest(TESTING_PACKAGE, "test16", true, IncrementalProjectBuilder.INCREMENTAL_BUILD, true);
	}
	
	/**
	 * Tests the unsupported @nooverride tag on an annotation field
	 * using a full build
	 */
	public void testInvalidAnnotationFieldTag16F() {
		setExpectedProblemIds(getDefaultProblemSet(1));
		deployTagTest(TESTING_PACKAGE, "test16", true, IncrementalProjectBuilder.FULL_BUILD, true);
	}
	
	/**
	 * Tests the unsupported @nooverride tag on an outer annotation field
	 * using an incremental build
	 */
	public void testInvalidAnnotationFieldTag17I() {
		setExpectedProblemIds(getDefaultProblemSet(1));
		deployTagTest(TESTING_PACKAGE, "test17", true, IncrementalProjectBuilder.INCREMENTAL_BUILD, true);
	}
	
	/**
	 * Tests the unsupported @nooverride tag on an outer annotation field
	 * using a full build
	 */
	public void testInvalidAnnotationFieldTag17F() {
		setExpectedProblemIds(getDefaultProblemSet(1));
		deployTagTest(TESTING_PACKAGE, "test17", true, IncrementalProjectBuilder.FULL_BUILD, true);
	}
	
	/**
	 * Tests the unsupported @nooverride tag on an inner annotation field
	 * using an incremental build
	 */
	public void testInvalidAnnotationFieldTag18I() {
		setExpectedProblemIds(getDefaultProblemSet(1));
		deployTagTest(TESTING_PACKAGE, "test18", true, IncrementalProjectBuilder.INCREMENTAL_BUILD, true);
	}
	
	/**
	 * Tests the unsupported @nooverride tag on an inner annotation field
	 * using a full build
	 */
	public void testInvalidAnnotationFieldTag18F() {
		setExpectedProblemIds(getDefaultProblemSet(1));
		deployTagTest(TESTING_PACKAGE, "test18", true, IncrementalProjectBuilder.FULL_BUILD, true);
	}
	
	/**
	 * Tests the unsupported @nooverride tag on a variety of inner / outer annotation fields
	 * using an incremental build
	 */
	public void testInvalidAnnotationFieldTag19I() {
		setExpectedProblemIds(getDefaultProblemSet(3));
		deployTagTest(TESTING_PACKAGE, "test19", true, IncrementalProjectBuilder.INCREMENTAL_BUILD, true);
	}
	
	/**
	 * Tests the unsupported @nooverride tag on a variety of inner / outer annotation fields
	 * using a full build
	 */
	public void testInvalidAnnotationFieldTag19F() {
		setExpectedProblemIds(getDefaultProblemSet(3));
		deployTagTest(TESTING_PACKAGE, "test19", true, IncrementalProjectBuilder.FULL_BUILD, true);
	}
	
	/**
	 * Tests the unsupported @nooverride tag on an annotation field in the default package
	 * using an incremental build
	 */
	public void testInvalidAnnotationFieldTag20I() {
		setExpectedProblemIds(getDefaultProblemSet(1));
		deployTagTest("", "test20", true, IncrementalProjectBuilder.INCREMENTAL_BUILD, true);
	}
	
	/**
	 * Tests the unsupported @nooverride tag on an annotation field in the default package
	 * using a full build
	 */
	public void testInvalidAnnotationFieldTag20F() {
		setExpectedProblemIds(getDefaultProblemSet(1));
		deployTagTest("", "test20", true, IncrementalProjectBuilder.FULL_BUILD, true);
	}
	
	/**
	 * Tests all the unsupported tags on a variety of annotation fields
	 * using an incremental build
	 */
	public void testInvalidAnnotationFieldTag21I() {
		setExpectedProblemIds(getDefaultProblemSet(15));
		deployTagTest(TESTING_PACKAGE, "test21", true, IncrementalProjectBuilder.INCREMENTAL_BUILD, true);
	}
	
	/**
	 * Tests all the unsupported tags on a variety of annotation fields
	 * using a full build
	 */
	public void testInvalidAnnotationFieldTag21F() {
		setExpectedProblemIds(getDefaultProblemSet(15));
		deployTagTest(TESTING_PACKAGE, "test21", true, IncrementalProjectBuilder.FULL_BUILD, true);
	}
	
	/**
	 * Tests the unsupported @noreference tag on a final annotation field
	 * using an incremental build
	 */
	public void testInvalidAnnotationFieldTag22I() {
		setExpectedProblemIds(getDefaultProblemSet(1));
		deployTagTest(TESTING_PACKAGE, "test22", true, IncrementalProjectBuilder.INCREMENTAL_BUILD, true);
	}
	
	/**
	 * Tests the unsupported @noreference tag on a final annotation field
	 * using a full build
	 */
	public void testInvalidAnnotationFieldTag22F() {
		setExpectedProblemIds(getDefaultProblemSet(1));
		deployTagTest(TESTING_PACKAGE, "test22", true, IncrementalProjectBuilder.FULL_BUILD, true);
	}
	
	/**
	 * Tests the unsupported @noreference tag on a final outer annotation field
	 * using an incremental build
	 */
	public void testInvalidAnnotationFieldTag23I() {
		setExpectedProblemIds(getDefaultProblemSet(1));
		deployTagTest(TESTING_PACKAGE, "test23", true, IncrementalProjectBuilder.INCREMENTAL_BUILD, true);
	}
	
	/**
	 * Tests the unsupported @noreference tag on a final outer annotation field
	 * using a full build
	 */
	public void testInvalidAnnotationFieldTag23F() {
		setExpectedProblemIds(getDefaultProblemSet(1));
		deployTagTest(TESTING_PACKAGE, "test23", true, IncrementalProjectBuilder.FULL_BUILD, true);
	}
	
	/**
	 * Tests the unsupported @noreference tag on a final inner annotation field
	 * using an incremental build
	 */
	public void testInvalidAnnotationFieldTag24I() {
		setExpectedProblemIds(getDefaultProblemSet(1));
		deployTagTest(TESTING_PACKAGE, "test24", true, IncrementalProjectBuilder.INCREMENTAL_BUILD, true);
	}
	
	/**
	 * Tests the unsupported @noreference tag on a final inner annotation field
	 * using a full build
	 */
	public void testInvalidAnnotationFieldTag24F() {
		setExpectedProblemIds(getDefaultProblemSet(1));
		deployTagTest(TESTING_PACKAGE, "test24", true, IncrementalProjectBuilder.FULL_BUILD, true);
	}
	
	/**
	 * Tests the unsupported @noreference tag on a variety of final inner / outer annotation fields
	 * using an incremental build
	 */
	public void testInvalidAnnotationFieldTag25I() {
		setExpectedProblemIds(getDefaultProblemSet(3));
		deployTagTest(TESTING_PACKAGE, "test25", true, IncrementalProjectBuilder.INCREMENTAL_BUILD, true);
	}
	
	/**
	 * Tests the unsupported @noreference tag on a variety of final inner / outer annotation fields
	 * using a full build
	 */
	public void testInvalidAnnotationFieldTag25F() {
		setExpectedProblemIds(getDefaultProblemSet(3));
		deployTagTest(TESTING_PACKAGE, "test25", true, IncrementalProjectBuilder.FULL_BUILD, true);
	}
	
	/**
	 * Tests the unsupported @noreference tag on a final annotation field in the default package
	 * using an incremental build
	 */
	public void testInvalidAnnotationFieldTag26I() {
		setExpectedProblemIds(getDefaultProblemSet(1));
		deployTagTest("", "test26", true, IncrementalProjectBuilder.INCREMENTAL_BUILD, true);
	}
	
	/**
	 * Tests the unsupported @noreference tag on a final annotation field in the default package
	 * using a full build
	 */
	public void testInvalidAnnotationFieldTag26F() {
		setExpectedProblemIds(getDefaultProblemSet(1));
		deployTagTest("", "test26", true, IncrementalProjectBuilder.FULL_BUILD, true);
	}
}
