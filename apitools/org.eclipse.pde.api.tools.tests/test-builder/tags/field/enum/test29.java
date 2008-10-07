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
package a.b.c;

/**
 * Test unsupported @noinstantiate tag on fields in inner / outer enums
 */
public enum test29 {
	
	A;
	
	/**
	 * @noinstantiate
	 */
	public Object f1 = null;
	/**
	 * @noinstantiate
	 */
	protected int f2 = 0;
	/**
	 * @noinstantiate
	 */
	private char[] f3 = {};
	static enum inner {
		
		A;
		
		/**
		 * @noinstantiate
		 */
		public static Object f1 = null;
		/**
		 * @noinstantiate
		 */
		protected int f2 = 0;
		/**
		 * @noinstantiate
		 */
		private static char[] f3 = {};
		enum inner2 {
			
			A;
			
			/**
			 * @noinstantiate
			 */
			public Object f1 = null;
			/**
			 * @noinstantiate
			 */
			protected int f2 = 0;
			/**
			 * @noinstantiate
			 */
			private char[] f3 = {};
		}
	}
}

enum outer {
	
	A;
	
	/**
	 * @noinstantiate
	 */
	public Object f1 = null;
	/**
	 * @noinstantiate
	 */
	protected int f2 = 0;
	/**
	 * @noinstantiate
	 */
	private static char[] f3 = {};
}