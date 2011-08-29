/*
 * Copyright 2008 Google Inc.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.inouelab.coopqa.web.client;

import java.util.Vector;

import com.google.gwt.core.client.JsArray;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.visualization.client.Selectable;
import com.google.gwt.visualization.client.Selection;
import com.google.gwt.visualization.client.events.SelectHandler;

import org.inouelab.coopqa.web.shared.WebQuerySet;
import org.inouelab.coopqa.web.shared.WebResult;

;
/**
 * Handling selection event of the TreePage
 * Display appropriate result in the textbox
 * @see TreePage
 */
class TreeSelection extends SelectHandler {
	private final Selectable treeChart;
	private final Vector<WebQuerySet> setVec;
	private final WebResult result;
	private final TextArea textBox;

	/**
	 * 
	 * @param treeChart the {@link TreePage} object
	 * @param setVec the {@link WebQuerySet} {@link Vector}
	 * @param result the {@link WebResult} object
	 * @param textArea the result box
	 */
	TreeSelection(Selectable treeChart, Vector<WebQuerySet> setVec, WebResult result, TextArea textArea) {
		this.treeChart = treeChart;
		this.setVec = setVec;
		this.textBox = textArea;
		this.result = result;
	}

	@Override
	public void onSelect(SelectEvent event) {
		JsArray<Selection> s = getSelections();

		for (int i = 0; i < s.length(); ++i) {
			if (s.get(i).isRow()) {
				textBox.setText(result.printQuerySet(setVec.get(s.get(i).getRow())));
			}
		}
	}

	private JsArray<Selection> getSelections() {
		return treeChart.getSelections();
	}
}
