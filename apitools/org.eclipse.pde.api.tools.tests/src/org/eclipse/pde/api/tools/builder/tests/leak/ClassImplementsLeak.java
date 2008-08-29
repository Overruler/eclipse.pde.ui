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
package org.eclipse.pde.api.tools.builder.tests.leak;

import junit.framework.Test;

import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.runtime.IPath;
import org.eclipse.pde.api.tools.internal.problems.ApiProblemFactory;
import org.eclipse.pde.api.tools.internal.provisional.descriptors.IElementDescriptor;
import org.eclipse.pde.api.tools.internal.provisional.problems.IApiProblem;

/**
 * Tests that an API type that implements an internal type
 * is properly detected
 * 
 * @since 1.0
 */
public class ClassImplementsLeak extends LeakTest {

	private int pid = -1;
	
	/**
	 * Constructor
	 * @param name
	 */
	public ClassImplementsLeak(String name) {
		super(name);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.pde.api.tools.builder.tests.ApiBuilderTest#getDefaultProblemId()
	 */
	protected int getDefaultProblemId() {
		if(pid == -1) {
			pid = ApiProblemFactory.createProblemId(
					IApiProblem.CATEGORY_USAGE, 
					IElementDescriptor.T_REFERENCE_TYPE, 
					IApiProblem.API_LEAK, 
					IApiProblem.LEAK_IMPLEMENTS);
		}
		return pid;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.pde.api.tools.builder.tests.tags.InvalidFieldTagTests#getTestSourcePath()
	 */
	protected IPath getTestSourcePath() {
		return super.getTestSourcePath().append("class");
	}
	
	/**
	 * @return the test for this class
	 */
	public static Test suite() {
		return buildTestSuite(ClassImplementsLeak.class);
	}
	
	/**
	 * Tests that a class that implements an internal interface is properly flagged
	 * using a full build
	 */
	public void testClassImplementsLeak1F() {
		x1(false);
	}
	
	/**
	 * Tests that a class that implements an internal interface is properly flagged
	 * using an incremental build
	 */
	public void testClassImplementsLeak1I() {
		x1(true);
	}
	
	private void x1(boolean inc) {
		setExpectedProblemIds(new int[] {getDefaultProblemId()});
		String typename = "test8";
		setExpectedMessageArgs(new String[][] {{TESTING_INTERNAL_INTERFACE_NAME, typename}});
		deployLeakTest(new String[] {TESTING_PACKAGE, TESTING_PACKAGE_INTERNAL}, 
				new String[] {typename, TESTING_INTERNAL_INTERFACE_NAME},
				new String[] {TESTING_PACKAGE_INTERNAL}, 
				new String[] {TESTING_PACKAGE+"."+typename}, 
				true, 
				(inc ? IncrementalProjectBuilder.INCREMENTAL_BUILD : IncrementalProjectBuilder.FULL_BUILD), 
				true);
	}
	
	/**
	 * Tests that an outer class that implements an internal interface is not flagged
	 * using a full build
	 */
	public void testClassImplementsLeak2F() {
		x2(false);
	}
	
	/**
	 * Tests that an outer class that implements an internal interface is not flagged
	 * using an incremental build
	 */
	public void testClassImplementsLeak2I() {
		x2(true);
	}
	
	private void x2(boolean inc) {
		expectingNoProblems();
		String typename = "test9";
		deployLeakTest(new String[] {TESTING_PACKAGE, TESTING_PACKAGE_INTERNAL}, 
				new String[] {typename, TESTING_INTERNAL_INTERFACE_NAME},
				new String[] {TESTING_PACKAGE_INTERNAL}, 
				null, 
				false, 
				(inc ? IncrementalProjectBuilder.INCREMENTAL_BUILD : IncrementalProjectBuilder.FULL_BUILD), 
				true);
	}
	
	/**
	 * Tests that an inner class that implements an internal interface is not flagged
	 * using a full build
	 */
	public void testClassImplementsLeak3F() {
		x3(false);	
	}
	
	/**
	 * Tests that an inner class that implements an internal interface is not flagged
	 * using an incremental build
	 */
	public void testClassImplementsLeak3I() {
		x3(true);
	}
	
	private void x3(boolean inc) {
		expectingNoProblems();
		String typename = "test10";
		deployLeakTest(new String[] {TESTING_PACKAGE, TESTING_PACKAGE_INTERNAL}, 
				new String[] {typename, TESTING_INTERNAL_INTERFACE_NAME},
				new String[] {TESTING_PACKAGE_INTERNAL}, 
				null, 
				false, 
				(inc ? IncrementalProjectBuilder.INCREMENTAL_BUILD : IncrementalProjectBuilder.FULL_BUILD), 
				true);
	}
	
	/**
	 * Tests that a static inner class that implements an internal interface is not flagged
	 * using a full build
	 */
	public void testClassImplementsLeak4F() {
		x4(false);
	}
	
	/**
	 * Tests that a static inner class that implements an internal interface is not flagged
	 * using an incremental build
	 */
	public void testClassImplementsLeak4I() {
		x4(true);
	}
	
	private void x4(boolean inc) {
		expectingNoProblems();
		String typename = "test11";
		deployLeakTest(new String[] {TESTING_PACKAGE, TESTING_PACKAGE_INTERNAL}, 
				new String[] {typename, TESTING_INTERNAL_INTERFACE_NAME},
				new String[] {TESTING_PACKAGE_INTERNAL}, 
				null, 
				false, 
				(inc ? IncrementalProjectBuilder.INCREMENTAL_BUILD : IncrementalProjectBuilder.FULL_BUILD), 
				true);
	}
	
	/**
	 * Tests that a class that implements an internal interface is properly flagged
	 * using a full build even with an @noextend tag
	 */
	public void testClassImplementsLeak5F() {
		x5(false);
	}
	
	/**
	 * Tests that a class that implements an internal interface is properly flagged
	 * using an incremental build even with an @noextend tag
	 */
	public void testClassImplementsLeak5I() {
		x5(true);
	}
	
	private void x5(boolean inc) {
		setExpectedProblemIds(new int[] {getDefaultProblemId()});
		String typename = "test12";
		setExpectedMessageArgs(new String[][] {{TESTING_INTERNAL_INTERFACE_NAME, typename}});
		deployLeakTest(new String[] {TESTING_PACKAGE, TESTING_PACKAGE_INTERNAL}, 
				new String[] {typename, TESTING_INTERNAL_INTERFACE_NAME},
				new String[] {TESTING_PACKAGE_INTERNAL}, 
				new String[] {TESTING_PACKAGE+"."+typename}, 
				true, 
				(inc ? IncrementalProjectBuilder.INCREMENTAL_BUILD : IncrementalProjectBuilder.FULL_BUILD), 
				true);
	}
	
	/**
	 * Tests that a class that implements an internal interface is properly flagged
	 * using a full build even with an @noinstantiate tag
	 */
	public void testClassImplementsLeak6F() {
		x6(false);
	}
	
	/**
	 * Tests that a class that implements an internal interface is properly flagged
	 * using an incremental build even with an @noinstantiate tag
	 */
	public void testClassImplementsLeak6I() {
		x6(true);
	}
	
	private void x6(boolean inc) {
		setExpectedProblemIds(new int[] {getDefaultProblemId()});
		String typename = "test13";
		setExpectedMessageArgs(new String[][] {{TESTING_INTERNAL_INTERFACE_NAME, typename}});
		deployLeakTest(new String[] {TESTING_PACKAGE, TESTING_PACKAGE_INTERNAL}, 
				new String[] {typename, TESTING_INTERNAL_INTERFACE_NAME},
				new String[] {TESTING_PACKAGE_INTERNAL}, 
				new String[] {TESTING_PACKAGE+"."+typename}, 
				true, 
				(inc ? IncrementalProjectBuilder.INCREMENTAL_BUILD : IncrementalProjectBuilder.FULL_BUILD), 
				true);
	}
	
	/**
	 * Tests that a class that implements an internal interface is properly flagged
	 * using a full build even with an @noinstantiate and an @noextend tag
	 */
	public void testClassImplementsLeak7F() {
		x7(false);
	}
	
	/**
	 * Tests that a class that implements an internal interface is properly flagged
	 * using an incremental build even with an @noinstantiate and an @noextend tag
	 */
	public void testClassImplementsLeak7I() {
		x7(true);
	}
	
	private void x7(boolean inc) {
		setExpectedProblemIds(new int[] {getDefaultProblemId()});
		String typename = "test14";
		setExpectedMessageArgs(new String[][] {{TESTING_INTERNAL_INTERFACE_NAME, typename}});
		deployLeakTest(new String[] {TESTING_PACKAGE, TESTING_PACKAGE_INTERNAL}, 
				new String[] {typename, TESTING_INTERNAL_INTERFACE_NAME},
				new String[] {TESTING_PACKAGE_INTERNAL}, 
				new String[] {TESTING_PACKAGE+"."+typename}, 
				true, 
				(inc ? IncrementalProjectBuilder.INCREMENTAL_BUILD : IncrementalProjectBuilder.FULL_BUILD), 
				true);
	}
}