package org.inouelab.coopqa.web.client;

import java.util.Vector;

import org.inouelab.coopqa.web.shared.JobNotFinishedException;
import org.inouelab.coopqa.web.shared.ServerErrorException;
import org.inouelab.coopqa.web.shared.WebQuerySet;
import org.inouelab.coopqa.web.shared.WebResult;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.visualization.client.DataTable;
import com.google.gwt.visualization.client.VisualizationUtils;
import com.google.gwt.visualization.client.AbstractDataTable.ColumnType;
import com.google.gwt.visualization.client.visualizations.OrgChart;
import com.google.gwt.visualization.client.visualizations.OrgChart.Options;
import com.google.gwt.visualization.client.visualizations.OrgChart.Size;

/**
 * Displays a {@link org.inouelab.coopqa.base.Result} in the
 * format of a tree chart
 * @author Nam Dang
 *
 */
public class TreePage extends Composite {

	private static TreePageUiBinder uiBinder = GWT
			.create(TreePageUiBinder.class);
	private final ServiceAsync cqaService = GWT.create(Service.class);

	@UiField
	VerticalPanel panel;
	private WebResult result;
	private final TextArea textResult;
	private String id;

	interface TreePageUiBinder extends UiBinder<Widget, TreePage> {
	}

	public TreePage(String id) {
		initWidget(uiBinder.createAndBindUi(this));
		textResult = new TextArea();
		textResult.setSize("80%", "300px");
		textResult.setReadOnly(true);
		textResult.setStyleName("textBox");

		this.id = id.replace("/", "").replace("\\", "").replace(".", "");

		load();
	}

	private void load() {
		Window.setTitle("Job: " + id);
		panel.clear();
		result = null;

		panel.add(new HTML("<center><h1>Job ID: <b><i>" + id
				+ "</i></b></h1></center>"));

		// First retrieve the query from the server
		cqaService.getResult(id, new AsyncCallback<WebResult>() {

			@Override
			public void onSuccess(WebResult ret) {
				result = ret;

				// Load the graph
				VisualizationUtils.loadVisualizationApi(new Runnable() {
					@Override
					public void run() {
						build();
					}
				}, OrgChart.PACKAGE);
			}

			@Override
			public void onFailure(Throwable caught) {
				if (caught instanceof JobNotFinishedException) {
					panel.add(new HTML(
							"<div style='color:red; font-weight: bold;'><h2>Job not finished. Please refresh later.</h2></div>"));
					Anchor link = new Anchor("Refresh");
					link.addClickHandler(new ClickHandler() {

						@Override
						public void onClick(ClickEvent event) {
							load();
						}
					});

					panel.add(link);
				} else if (caught instanceof ServerErrorException) {
					panel.add(new HTML(
							"<div style='color:red; font-weight: bold;'><h2><b>ERROR:</b> "
									+ caught.getMessage() + " </h2></div>"));
					Anchor link = new Anchor("Refresh");
					link.addClickHandler(new ClickHandler() {

						@Override
						public void onClick(ClickEvent event) {
							load();
						}
					});

					panel.add(link);
				} else {
					panel.add(new HTML(
							"<div style='color:red; font-weight: bold;'><h2>Unable to locate the job id.<br />"
									+ "If you have just submitted the job, please check back in a few minutes.</h2></div>"));
					Anchor link = new Anchor("Go back");
					link.addClickHandler(new ClickHandler() {

						@Override
						public void onClick(ClickEvent event) {
							History.back();
						}
					});

					panel.add(link);
				}
			}
		});
	}

	private void build() {
		// Construct organization chart
		Options options = Options.create();
		options.setSize(Size.MEDIUM);
		options.setAllowCollapse(true);
		options.setColor("#ccffff");
		options.setSelectionColor("#ffbbbb");
		options.setOption("border-width", "2px");

		// Get a list of all vectors
		Vector<WebQuerySet> setVect = result.getRoot().getAllSets();

		// Construct the datatable
		DataTable data = DataTable.create();
		data.addColumn(ColumnType.STRING, "Operator");
		data.addColumn(ColumnType.STRING, "Parent");
		data.addColumn(ColumnType.STRING, "HoverText");

		data.addRows(setVect.size());
		for (int i = 0; i < setVect.size(); i++) {
			WebQuerySet set = setVect.get(i);
			data.setValue(i, 0, set.getOpStr());
			if (set.getParent() != null)
				data.setValue(i, 1, set.getParent().getOpStr());

			int qSize = set.size();
			if (qSize == 1)
				data.setValue(i, 2, "1 query");
			else
				data.setValue(i, 2, set.size() + " queries");
		}

		// Now create the org chat
		OrgChart treeChart = new OrgChart(data, options);

		// Now add the handler
		TreeSelection selectionHandler = new TreeSelection(treeChart, setVect,
				result, textResult);
		treeChart.addSelectHandler(selectionHandler);

		// Add to the panel
		panel.add(treeChart);

		// Text area
		panel.add(new Label());

		FlowPanel titlePanel = new FlowPanel();
		titlePanel.add(new Label("Queries and answers "));
		Anchor anchor = new Anchor("View all");
		anchor.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				textResult.setText(result.printAll());
			}
		});
		titlePanel.add(anchor);

		panel.add(titlePanel);
		panel.add(textResult);
	}
}
