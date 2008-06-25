/*******************************************************************************
 * Copyright (c) 2008 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     Rafael Oliveira N�brega <rafael.oliveira@gmail.com> - bug 233997
 *******************************************************************************/
package org.eclipse.pde.internal.ds.ui.editor;

import java.util.ArrayList;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.contentassist.ContentAssistEvent;
import org.eclipse.jface.text.contentassist.ICompletionListener;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.text.contentassist.IContentAssistProcessor;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.pde.core.IBaseModel;
import org.eclipse.pde.internal.core.text.AbstractEditingModel;
import org.eclipse.pde.internal.core.text.IDocumentAttributeNode;
import org.eclipse.pde.internal.core.text.IDocumentElementNode;
import org.eclipse.pde.internal.core.text.IDocumentRange;
import org.eclipse.pde.internal.core.text.IDocumentTextNode;
import org.eclipse.pde.internal.core.text.IReconcilingParticipant;
import org.eclipse.pde.internal.ds.core.IDSComponent;
import org.eclipse.pde.internal.ds.core.IDSConstants;
import org.eclipse.pde.internal.ds.core.IDSModel;
import org.eclipse.pde.internal.ds.core.IDSProvide;
import org.eclipse.pde.internal.ds.core.IDSService;
import org.eclipse.pde.internal.ds.core.text.DSModel;
import org.eclipse.pde.internal.ds.core.text.DSObject;
import org.eclipse.pde.internal.ds.ui.IConstants;
import org.eclipse.pde.internal.ds.ui.editor.contentassist.TypeCompletionProposal;
import org.eclipse.pde.internal.ui.editor.PDESourcePage;
import org.eclipse.pde.internal.ui.editor.contentassist.TypePackageCompletionProcessor;

public class DSContentAssistProcessor extends TypePackageCompletionProcessor
		implements IContentAssistProcessor, ICompletionListener {
	protected boolean fAssistSessionStarted;

	private PDESourcePage fSourcePage;

	private IDocumentRange fRange;

	// proposal generation type
	private static final int F_NO_ASSIST = 0, F_ADD_ATTRIB = 1,
			F_ADD_CHILD = 2, F_OPEN_TAG = 3;

	public DSContentAssistProcessor(PDESourcePage sourcePage) {
		fSourcePage = sourcePage;
	}

	public void assistSessionEnded(ContentAssistEvent event) {
	}

	public void assistSessionStarted(ContentAssistEvent event) {
	}

	public void selectionChanged(ICompletionProposal proposal,
			boolean smartToggle) {
	}

	public ICompletionProposal[] computeCompletionProposals(ITextViewer viewer,
			int offset) {
		IDocument doc = viewer.getDocument();
		IBaseModel model = fSourcePage.getInputContext().getModel();

		if (model instanceof AbstractEditingModel && fSourcePage.isDirty()
				&& ((AbstractEditingModel) model).isStale() && fRange == null) {
			((AbstractEditingModel) model).reconciled(doc);
		} else if (fAssistSessionStarted) {
			// Always reconcile when content assist is first invoked
			// Fix Bug # 149478
			((AbstractEditingModel) model).reconciled(doc);
			fAssistSessionStarted = false;
		}

		if (fRange == null) {
			assignRange(offset);
		} else {
			// TODO - we may be looking at the wrong fRange
			// when this happens --> reset it and reconcile
			// how can we tell if we are looking at the wrong one... ?
			boolean resetAndReconcile = false;
			if (!(fRange instanceof IDocumentAttributeNode))
				// too easy to reconcile.. this is temporary
				resetAndReconcile = true;

			if (resetAndReconcile) {
				fRange = null;
				if (model instanceof IReconcilingParticipant)
					((IReconcilingParticipant) model).reconciled(doc);
			}
		}
		// Get content assist text if any
		DSContentAssistText caText = DSContentAssistText.parse(offset, doc);

		if (caText != null) {
			return stubProposals(model, "CAText", offset);
			// return computeCATextProposal(doc, offset, caText);
		} else if (fRange instanceof IDocumentAttributeNode) {
			return computeCompletionProposal((IDocumentAttributeNode) fRange,
					offset, doc);
		} else if (fRange instanceof IDocumentElementNode) {
			return computeCompletionProposal((IDocumentElementNode) fRange,
					offset, doc);
		} else if (fRange instanceof IDocumentTextNode) {
			// TODO How do I reach here?! by rafael
			return stubProposals(model, "Text", offset);
		}

		return null;
	}

	private ICompletionProposal[] computeCompletionProposal(
			IDocumentAttributeNode attr, int offset, IDocument doc) {
		if (offset < attr.getValueOffset())
			return null;
		int[] offests = new int[] { offset, offset, offset };
		String[] guess = guessContentRequest(offests, doc, false);
		if (guess == null)
			return null;

		String element = guess[0];
		String attribute = guess[1];
		String attrValue = guess[2];
		int attrValueLength = attrValue == null ? 0 : attrValue.length();
		int startOffset = offests[2] + 1;

		if (element != null && element.equals(IDSConstants.ELEMENT_COMPONENT)) {
			boolean isAttrImmediate = attribute == null ? false : attribute
					.equals(IDSConstants.ATTRIBUTE_COMPONENT_IMMEDIATE);
			boolean isAttrEnabled = attribute == null ? false : attribute
					.equals(IDSConstants.ATTRIBUTE_COMPONENT_ENABLED);
			if ((isAttrImmediate || isAttrEnabled)) {

				return this.getCompletionBooleans(startOffset, attrValueLength);
			}
		} else if (element != null
				&& element.equals(IDSConstants.ELEMENT_SERVICE)) {
			boolean isAttrServFactory = attribute == null ? false : attribute
					.equals(IDSConstants.ATTRIBUTE_SERVICE_FACTORY);
			if (isAttrServFactory) {
				return this.getCompletionBooleans(startOffset, attrValueLength);
			}
		} else if (element != null
				&& element.equals(IDSConstants.ELEMENT_REFERENCE)) {
			boolean isAttrCardinality = attribute == null ? false : attribute
					.equals(IDSConstants.ATTRIBUTE_REFERENCE_CARDINALITY);
			boolean isAttrPolicy = attribute == null ? false : attribute
					.equals(IDSConstants.ATTRIBUTE_REFERENCE_POLICY);
			if (isAttrCardinality) {
				return getReferenceCardinalityValues(attrValueLength,
						startOffset);
			} else if (isAttrPolicy) {
				return getReferencePolicyValues(attrValueLength, startOffset);

			}
		} else if (element != null
				&& element.equals(IDSConstants.ELEMENT_PROPERTY)) {
			boolean isAttrType = attribute == null ? false : attribute
					.equals(IDSConstants.ATTRIBUTE_PROPERTY_TYPE);
			if (isAttrType) {
				return getPropertyTypeValues(attrValueLength, startOffset);
			}

		}
		return null;
	}

	private ICompletionProposal[] getReferencePolicyValues(int attrValueLength,
			int startOffset) {
		return new ICompletionProposal[] {
				new TypeCompletionProposal(IConstants.REFERENCE_STATIC, null,
						IConstants.REFERENCE_STATIC, startOffset,
						attrValueLength),
				new TypeCompletionProposal(IConstants.REFERENCE_DYNAMIC, null,
						IConstants.REFERENCE_DYNAMIC, startOffset,
						attrValueLength) };
	}

	private ICompletionProposal[] getReferenceCardinalityValues(
			int attrValueLength, int startOffset) {
		return new ICompletionProposal[] {
				new TypeCompletionProposal(IConstants.CARDINALITY_ZERO_ONE,
						null, IConstants.CARDINALITY_ZERO_ONE, startOffset,
						attrValueLength),
				new TypeCompletionProposal(IConstants.CARDINALITY_ZERO_N, null,
						IConstants.CARDINALITY_ZERO_N, startOffset,
						attrValueLength),
				new TypeCompletionProposal(IConstants.CARDINALITY_ONE_ONE,
						null, IConstants.CARDINALITY_ONE_ONE, startOffset,
						attrValueLength),
				new TypeCompletionProposal(IConstants.CARDINALITY_ONE_N, null,
						IConstants.CARDINALITY_ONE_N, startOffset,
						attrValueLength) };
	}

	private ICompletionProposal[] getPropertyTypeValues(int attrValueLength,
			int startOffset) {
		return new ICompletionProposal[] {
				new TypeCompletionProposal(IConstants.PROPERTY_TYPE_STRING,
						null, IConstants.PROPERTY_TYPE_STRING, startOffset,
						attrValueLength),
				new TypeCompletionProposal(IConstants.PROPERTY_TYPE_LONG, null,
						IConstants.PROPERTY_TYPE_LONG, startOffset,
						attrValueLength),
				new TypeCompletionProposal(IConstants.PROPERTY_TYPE_DOUBLE,
						null, IConstants.PROPERTY_TYPE_DOUBLE, startOffset,
						attrValueLength),
				new TypeCompletionProposal(IConstants.PROPERTY_TYPE_FLOAT,
						null, IConstants.PROPERTY_TYPE_FLOAT, startOffset,
						attrValueLength),
				new TypeCompletionProposal(IConstants.PROPERTY_TYPE_INTEGER,
						null, IConstants.PROPERTY_TYPE_INTEGER, startOffset,
						attrValueLength),
				new TypeCompletionProposal(IConstants.PROPERTY_TYPE_BYTE, null,
						IConstants.PROPERTY_TYPE_BYTE, startOffset,
						attrValueLength),
				new TypeCompletionProposal(IConstants.PROPERTY_TYPE_CHAR, null,
						IConstants.PROPERTY_TYPE_CHAR, startOffset,
						attrValueLength),
				new TypeCompletionProposal(IConstants.PROPERTY_TYPE_BOOLEAN,
						null, IConstants.PROPERTY_TYPE_BOOLEAN, startOffset,
						attrValueLength),
				new TypeCompletionProposal(IConstants.PROPERTY_TYPE_SHORT,
						null, IConstants.PROPERTY_TYPE_SHORT, startOffset,
						attrValueLength) };
	}

	private ICompletionProposal[] getCompletionBooleans(int startOffset,
			int attrValueLength) {

		return new ICompletionProposal[] {
				new TypeCompletionProposal(IConstants.TRUE, null,
						IConstants.TRUE, startOffset, attrValueLength),
				new TypeCompletionProposal(IConstants.FALSE, null,
						IConstants.FALSE, startOffset, attrValueLength) };
	}

	private ICompletionProposal[] computeCompletionProposal(
			IDocumentElementNode node, int offset, IDocument doc) {
		int prop_type = determineAssistType(node, doc, offset);
		switch (prop_type) {
		case F_ADD_ATTRIB:
			return computeAddAttributeProposal(node, offset, doc, null, node
					.getXMLTagName());
		case F_OPEN_TAG:
			return stubProposals((DSModel) fSourcePage.getInputContext()
					.getModel(), "Open Tag", offset);
		case F_ADD_CHILD:
			return computeAddChildProposal(node, offset, doc, null);
		}
		return null;
	}

	private ICompletionProposal[] computeAddAttributeProposal(
			IDocumentElementNode node, int offset, IDocument doc,
			String filter, String tag) {

		ArrayList proposals = new ArrayList();
		String[] attributesList = this.getAttributesList(tag);
		
		if (attributesList == null || attributesList.length == 0) {
			return null;
		} else {
			for (int i = 0; i < attributesList.length; i++) {
				String attribute = attributesList[i];
				// Lists all attributes already in use
				IDocumentAttributeNode[] nodeAttributes = node
						.getNodeAttributes();
				boolean EqualToAnyItem = false;
				for (int j = 0; j < nodeAttributes.length; j++) {
					IDocumentAttributeNode documentAttributeNode = nodeAttributes[j];
					EqualToAnyItem |= attribute.equals(documentAttributeNode
							.getAttributeName());

				}
				// If the attribute is not in use, add it in the
				// CompletionProposal
				if (EqualToAnyItem == false) {
					DSAttrCompletionProposal dsAttrCompletionProposal = new DSAttrCompletionProposal(
							attributesList[i], offset, 0);
					proposals.add(dsAttrCompletionProposal);	
				}
				

			}
		}

		// cast the proposal elements to ICompletionProposal
		if (proposals.size() > 0) {
			ICompletionProposal proposalsArray[] = new ICompletionProposal[proposals
					.size()];
			for (int i = 0; i < proposals.size(); i++) {
				proposalsArray[i] = (ICompletionProposal) proposals.get(i);

			}
			return proposalsArray;
		} else {
			return null;
		}
	}

	private ICompletionProposal[] computeAddChildProposal(
			IDocumentElementNode node, int offset, IDocument doc, String filter) {
		ArrayList propList = new ArrayList();
		if (node instanceof IDSComponent) {
			return computeRootNodeProposals(node, offset, filter);
		} else if (node instanceof IDSService) {
			DSModel model = (DSModel) fSourcePage.getInputContext().getModel();
			IDSProvide provide = model.getFactory().createProvide();
			return new DSCompletionProposal[] { new DSCompletionProposal(
					provide, offset) };
		} else {
			return null;
		}
	}
	
	private String[] getAttributesList(String element) {
		String[] attributes = null;
		if (element != null) {
			if (element.equals(IDSConstants.ELEMENT_COMPONENT)) {
				attributes = new String[] {
						IDSConstants.ATTRIBUTE_COMPONENT_ENABLED,
						IDSConstants.ATTRIBUTE_COMPONENT_FACTORY,
						IDSConstants.ATTRIBUTE_COMPONENT_IMMEDIATE,
						IDSConstants.ATTRIBUTE_COMPONENT_NAME };
			} else if (element.equals(IDSConstants.ELEMENT_IMPLEMENTATION)) {
				attributes = new String[] { IDSConstants.ATTRIBUTE_IMPLEMENTATION_CLASS };

			} else if (element.equals(IDSConstants.ELEMENT_PROPERTIES)) {
				attributes = new String[] { IDSConstants.ATTRIBUTE_PROPERTIES_ENTRY };

			} else if (element.equals(IDSConstants.ELEMENT_PROPERTY)) {
				attributes = new String[] {
						IDSConstants.ATTRIBUTE_PROPERTY_NAME,
						IDSConstants.ATTRIBUTE_PROPERTY_TYPE,
						IDSConstants.ATTRIBUTE_PROPERTY_VALUE };

			} else if (element.equals(IDSConstants.ELEMENT_PROVIDE)) {
				attributes = new String[] { IDSConstants.ATTRIBUTE_PROVIDE_INTERFACE };

			} else if (element.equals(IDSConstants.ELEMENT_REFERENCE)) {
				attributes = new String[] {
						IDSConstants.ATTRIBUTE_REFERENCE_BIND,
						IDSConstants.ATTRIBUTE_REFERENCE_CARDINALITY,
						IDSConstants.ATTRIBUTE_REFERENCE_INTERFACE,
						IDSConstants.ATTRIBUTE_REFERENCE_NAME,
						IDSConstants.ATTRIBUTE_REFERENCE_POLICY,
						IDSConstants.ATTRIBUTE_REFERENCE_TARGET,
						IDSConstants.ATTRIBUTE_REFERENCE_UNBIND };

			} else if (element.equals(IDSConstants.ELEMENT_SERVICE)) {
				attributes = new String[] { IDSConstants.ATTRIBUTE_SERVICE_FACTORY };
			}
		}
		return attributes;
	}

	private ICompletionProposal[] computeRootNodeProposals(
			IDocumentElementNode node, int offset, String filter) {
		ArrayList proposals = new ArrayList();
		IDSModel model = (DSModel) fSourcePage.getInputContext().getModel();

		IDSComponent component = model.getDSComponent();

		proposals.add(new DSCompletionProposal(model.getFactory()
				.createProperty(), offset));
		proposals.add(new DSCompletionProposal(model.getFactory()
				.createProperties(), offset));
		proposals.add(new DSCompletionProposal(model.getFactory()
				.createReference(), offset));
		boolean hasService = component.getService() != null;
		if (!hasService) {
			proposals.add(new DSCompletionProposal(model.getFactory()
					.createService(), offset));
		}

		if (component.getImplementation() == null) {
			proposals.add(new DSCompletionProposal(model.getFactory()
					.createImplementation(), offset));
		}

		ICompletionProposal[] proposalsArray = new DSCompletionProposal[proposals
				.size()];
		for (int i = 0; i < proposalsArray.length; i++) {
			proposalsArray[i] = (ICompletionProposal) proposals.get(i);
		}
		return proposalsArray;

	}

	private int determineAssistType(IDocumentElementNode node, IDocument doc,
			int offset) {
		int len = node.getLength();
		int off = node.getOffset();
		if (len == -1 || off == -1)
			return F_NO_ASSIST;

		offset = offset - off; // look locally
		if (offset > node.getXMLTagName().length() + 1) {
			try {
				String eleValue = doc.get(off, len);
				int ind = eleValue.indexOf('>');
				if (ind > 0 && eleValue.charAt(ind - 1) == '/')
					ind -= 1;
				if (offset <= ind) {
					if (canInsertAttrib(eleValue, offset))
						return F_ADD_ATTRIB;
					return F_NO_ASSIST;
				}
				ind = eleValue.lastIndexOf('<');
				if (ind == 0 && offset == len - 1)
					return F_OPEN_TAG; // childless node - check if it can be
				// cracked open
				if (ind + 1 < len && eleValue.charAt(ind + 1) == '/'
						&& offset <= ind)
					return F_ADD_CHILD;
			} catch (BadLocationException e) {
			}
		}
		return F_NO_ASSIST;
	}

	private boolean canInsertAttrib(String eleValue, int offset) {
		// character before offset must be whitespace
		// character on offset must be whitespace, '/' or '>'
		char c = eleValue.charAt(offset);
		return offset - 1 >= 0
				&& Character.isWhitespace(eleValue.charAt(offset - 1))
				&& (Character.isWhitespace(c) || c == '/' || c == '>');
	}

	private String[] guessContentRequest(int[] offset, IDocument doc,
			boolean brokenModel) {
		StringBuffer nodeBuffer = new StringBuffer();
		StringBuffer attrBuffer = new StringBuffer();
		StringBuffer attrValBuffer = new StringBuffer();
		String node = null;
		String attr = null;
		String attVal = null;
		int quoteCount = 0;
		try {
			while (--offset[0] >= 0) {
				char c = doc.getChar(offset[0]);
				if (c == '"') {
					quoteCount += 1;
					nodeBuffer.setLength(0);
					attrBuffer.setLength(0);
					if (attVal != null) // ran into 2nd quotation mark, we are
						// out of range
						continue;
					offset[2] = offset[0];
					attVal = attrValBuffer.toString();
				} else if (Character.isWhitespace(c)) {
					nodeBuffer.setLength(0);
					if (attr == null) {
						offset[1] = offset[0];
						int attBuffLen = attrBuffer.length();
						if (attBuffLen > 0
								&& attrBuffer.charAt(attBuffLen - 1) == '=')
							attrBuffer.setLength(attBuffLen - 1);
						attr = attrBuffer.toString();
					}
				} else if (c == '<') {
					node = nodeBuffer.toString();
					break;
				} else if (c == '>') {
					// only enable content assist if user is inside an open tag
					return null;
				} else {
					attrValBuffer.insert(0, c);
					attrBuffer.insert(0, c);
					nodeBuffer.insert(0, c);
				}
			}
		} catch (BadLocationException e) {
		}
		if (node == null)
			return null;

		if (quoteCount % 2 == 0)
			attVal = null;
		else if (brokenModel)
			return null; // open quotes - don't provide assist

		return new String[] { node, attr, attVal };
	}

	/**
	 * This is a temporary method to help tracking the Content Assist Range
	 * (Should be remove before this class is done)
	 * 
	 * @param model
	 * @param string
	 * @return
	 */
	private DSCompletionProposal[] stubProposals(IBaseModel model,
			String string, int offset) {
		DSObject component = new DSObject((DSModel) model, "Stub:" + string) {

			public boolean canAddChild(int objectType) {
				// TODO Auto-generated method stub
				return false;
			}

			public boolean canBeParent() {
				// TODO Auto-generated method stub
				return false;
			}

			public String getName() {
				return this.getXMLTagName();
			}

			public int getType() {
				return -1;
			}
		};
		DSCompletionProposal[] proposals = new DSCompletionProposal[] { new DSCompletionProposal(
				component, offset) };
		return proposals;
	}

	private void assignRange(int offset) {
		fRange = fSourcePage.getRangeElement(offset, true);
		if (fRange == null)
			return;
		// if we are rigth AT (cursor before) the range, we want to contribute
		// to its parent
		if (fRange instanceof IDocumentAttributeNode) {
			if (((IDocumentAttributeNode) fRange).getNameOffset() == offset)
				fRange = ((IDocumentAttributeNode) fRange)
						.getEnclosingElement();
		} else if (fRange instanceof IDocumentElementNode) {
			if (((IDocumentElementNode) fRange).getOffset() == offset)
				fRange = ((IDocumentElementNode) fRange).getParentNode();
		} else if (fRange instanceof IDocumentTextNode) {
			if (((IDocumentTextNode) fRange).getOffset() == offset)
				fRange = ((IDocumentTextNode) fRange).getEnclosingElement();
		}
	}

	public void dispose() {

	}

	protected ITextSelection getCurrentSelection() {
		ISelection sel = fSourcePage.getSelectionProvider().getSelection();
		if (sel instanceof ITextSelection)
			return (ITextSelection) sel;
		return null;
	}

	protected void flushDocument() {
		fSourcePage.getInputContext().flushEditorInput();
	}

}
