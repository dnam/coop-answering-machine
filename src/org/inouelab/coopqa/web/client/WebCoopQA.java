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

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.VerticalPanel;


/**
 * Google Visualization API in GWT demo.
 */
class WebCoopQA implements EntryPoint, ValueChangeHandler<String>  {

	private final SubmitPage uploadForm = new SubmitPage();
	public void onModuleLoad() {
		if (History.getToken().length() != 0) {
			changePage(History.getToken());
			return;
		}
		
		History.addValueChangeHandler(this);
		
		final VerticalPanel vp = new VerticalPanel();
		
		vp.setWidth("100%");
		RootPanel.get().add(uploadForm);
	}
	
	public void changePage(String token) {
		final TreePage tree = new TreePage(token);
		RootPanel.get().clear();
		RootPanel.get().add(tree);
	}

	@Override
	public void onValueChange(ValueChangeEvent<String> event) {
		if (event.getValue().length() != 0) {
			changePage(History.getToken());
			return;
		}
		else
		{
			RootPanel.get().clear();
			RootPanel.get().add(uploadForm);
		}
	}

}
