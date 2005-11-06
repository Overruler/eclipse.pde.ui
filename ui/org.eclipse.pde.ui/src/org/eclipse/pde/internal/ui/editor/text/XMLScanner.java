/*******************************************************************************
 * Copyright (c) 2000, 2004 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.pde.internal.ui.editor.text;

import org.eclipse.jface.text.TextAttribute;
import org.eclipse.jface.text.rules.IRule;
import org.eclipse.jface.text.rules.RuleBasedScanner;
import org.eclipse.jface.text.rules.SingleLineRule;
import org.eclipse.jface.text.rules.Token;
import org.eclipse.jface.text.rules.WhitespaceRule;
import org.eclipse.jface.util.PropertyChangeEvent;

public class XMLScanner extends RuleBasedScanner {
	private Token fProcInstr;

	public XMLScanner(IColorManager manager) {
		fProcInstr = new Token(new TextAttribute(manager
				.getColor(IPDEColorConstants.P_PROC_INSTR)));
		
		IRule[] rules = new IRule[2];		
		//Add rule for processing instructions
		rules[0] = new SingleLineRule("<?", "?>", fProcInstr); //$NON-NLS-1$ //$NON-NLS-2$
		// Add generic whitespace rule.
		rules[1] = new WhitespaceRule(new XMLWhitespaceDetector());
		setRules(rules);
	}
	
	private Token getTokenAffected(PropertyChangeEvent event) {
    	if (event.getProperty().startsWith(IPDEColorConstants.P_PROC_INSTR)) {
    		return fProcInstr;
    	}
    	return (Token)fDefaultReturnToken;
    }
    
    public void adaptToPreferenceChange(IColorManager colorManager, PropertyChangeEvent event) {
    	String property= event.getProperty();
    	if (property.startsWith(IPDEColorConstants.P_DEFAULT) || property.startsWith(IPDEColorConstants.P_PROC_INSTR)) {
    		Token token = getTokenAffected(event);
			TextAttribute attr= (TextAttribute) token.getData();
			token.setData(new TextAttribute(colorManager.getColor(event.getProperty()), attr.getBackground(), attr.getStyle()));
    	}
    }
    
}
