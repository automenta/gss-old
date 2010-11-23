package automenta.survive.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.maps.client.MapWidget;
import com.google.gwt.maps.client.Maps;
import com.google.gwt.maps.client.control.LargeMapControl;
import com.google.gwt.maps.client.event.MapMoveEndHandler;
import com.google.gwt.maps.client.event.MapMoveEndHandler.MapMoveEndEvent;
import com.google.gwt.maps.client.geom.LatLng;
import com.google.gwt.maps.client.overlay.Overlay;
import com.google.gwt.maps.client.overlay.Polygon;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.RootLayoutPanel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.SplitLayoutPanel;
import com.google.gwt.user.client.ui.Widget;
import java.util.LinkedList;
import java.util.List;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class Survive implements EntryPoint {
//	/**
//	 * The message displayed to the user when the server cannot be reached or
//	 * returns an error.
//	 */
//	private static final String SERVER_ERROR = "An error occurred while "
//			+ "attempting to contact the server. Please check your network "
//			+ "connection and try again.";

    //Netention
    //String mapKey = "ABQIAAAA3D2mD_qMSK4fmuGtL57T-xSDojzdHL0eXAbqS1KdjvUfYV59gRRXBYd70xT7XGWMayM0LgCk0pLVWg";

    //Omnidelic
    String mapKey = "ABQIAAAA3D2mD_qMSK4fmuGtL57T-xQui6zwImYgkXgNzwtgZ9QM8u9rqRQc_wXgcyiolM6BGKIq1NJ6MBzU9A";

    private MapWidget map;
    int resolution = 8;
    final String presetImmediateSurvival = "Immediate Survival";
    final String presetHunterGatherer = "Hunter-Gatherer";
    final String presetAgriculturable = "Agriculture-Able";
    final String presetOutdoorCamping = "Outdoor Camping";
    final String preset3rdWorld = "3rd World Urban";
    final String preset1stWorldLower = "1st World - Lower Class";
    final String preset1stWorldMiddle = "1st World - Middle Class";
    final String preset1stWorldUpper = "1st World - Upper Class";

    // GWT module entry point method.
    public void onModuleLoad() {
        /*
         * Asynchronously loads the Maps API.
         *
         * The first parameter should be a valid Maps API Key to deploy this
         * application on a public server, but a blank key will work for an
         * application served from localhost.
         */

        Maps.loadMapsApi(mapKey, "2", false, new Runnable() {

            public void run() {
                buildUi();
                updateMap();
            }
        });
    }
    List<Overlay> overlays = new LinkedList();

    protected void updateMap() {
        //remove old overlays
        for (Overlay o : overlays) {
            map.removeOverlay(o);
        }

        overlays.clear();

        //add overlays
        addOverlays();
    }

    protected void addOverlay(Overlay o) {
        overlays.add(o);
        map.addOverlay(o);
    }

    protected void addSquare(double lon, double lat, double width, double height, String color, double opacity) {
        LatLng[] points = new LatLng[4];

        double wh = width / 2.0;
        double hh = height / 2.0;

        points[0] = LatLng.newInstance(lat - hh, lon - wh);
        points[1] = LatLng.newInstance(lat - hh, lon + wh);
        points[2] = LatLng.newInstance(lat + hh, lon + wh);
        points[3] = LatLng.newInstance(lat + hh, lon - wh);
        String strokeColor = "#000000";
        int strokeWidth = 0;
        double strokeOpacity = 0;
        Polygon p = new Polygon(points, strokeColor, strokeWidth, strokeOpacity, color, opacity);
        addOverlay(p);

    }

    protected void addOverlays() {

        double border = 0.0;
        double width = map.getBounds().toSpan().getLongitude() * (1.0 - border);
        double height = map.getBounds().toSpan().getLatitude() * (1.0 - border);

        double vw = width / ((double) resolution + 1);
        double vh = height / ((double) resolution + 1);

        double cy = map.getCenter().getLatitude() - height / 2.0 + vh / 4.0;
        for (int x = -resolution / 2; x < resolution / 2; x++) {
            double cx = map.getCenter().getLongitude() - width / 2.0 + vw;
            for (int y = -resolution / 2; y < resolution / 2; y++) {
                addSquare(cx, cy, vw, vh, "#00ff00", Math.random() / 2.0);
                cx += vw;
            }
            cy += vh;
        }

    }

    public static class Spinner extends Grid {

        Button upButton = new Button("+");
        Button downButton = new Button("-");
        Label display = new Label("");
        private int value;
        private final int min;
        private final int max;

        public Spinner(int min, int max, int current) {
            super(1, 3);
           
            this.min = min;
            this.max = max;

            setWidget(0, 0, downButton);
            setWidget(0, 1, display);
            setWidget(0, 2, upButton);
            setValue(current);

            downButton.addClickHandler(new ClickHandler() {

                public void onClick(ClickEvent ce) {
                    setValue(getValue() - 1);
                }
            });
            upButton.addClickHandler(new ClickHandler() {

                public void onClick(ClickEvent ce) {
                    setValue(getValue() + 1);
                }
            });
        }

        protected void setValue(int newValue) {
            this.value = newValue;
            this.value = Math.min(max, value);
            this.value = Math.max(min, value);
            this.display.setText(Integer.toString(value));
        }

        public int getValue() {
            return value;
        }
    }

    protected Widget newMeasurementFactor(String label) {
        FlowPanel p = new FlowPanel();
        p.setWidth("100%");

        Label l = new Label(label);
        l.addStyleName("bigLabel");
        p.add(l);
        
        Spinner s = new Spinner(0, 10, 0);
        p.add(s);

        return p;
    }

    protected Panel getControls() {
        //DockLayoutPanel dp = new DockLayoutPanel(Unit.PCT);
        FlowPanel panel = new FlowPanel();

        FlowPanel topPanel = new FlowPanel();
        topPanel.addStyleName("topPanel");
        {
            topPanel.add(new Label("Detail"));
            topPanel.add(new Spinner(4, 16, 8) {

                @Override
                protected void setValue(int newValue) {
                    super.setValue(newValue);
                    resolution = getValue();
                    updateMap();
                }
            });

            topPanel.add(new Label("Preset"));

            ListBox preset = new ListBox();
            preset.addItem(presetImmediateSurvival);
            preset.addItem(presetHunterGatherer);
            preset.addItem(presetAgriculturable);
            preset.addItem(presetOutdoorCamping);
            preset.addItem(preset3rdWorld);
            preset.addItem(preset1stWorldLower);
            preset.addItem(preset1stWorldMiddle);
            preset.addItem(preset1stWorldUpper);

            topPanel.add(preset);
        }
        panel.add(topPanel);


        
//            Weather: temperature, pressure, wind, etc...
        panel.add(newMeasurementFactor("Weather"));

//            Water and food: availability of nutrients and utilities "Herbs"
        panel.add(newMeasurementFactor("Water and Food"));

//            Shelter
        panel.add(newMeasurementFactor("Shelter"));

//            Safety (lack of crime and violence)
        panel.add(newMeasurementFactor("Safety"));

//            Lack of pollutants
        panel.add(newMeasurementFactor("Lack of Pollution"));

//            Medicine and healthcare
        panel.add(newMeasurementFactor("Medicine and Healthcare"));

//            Energy (Gas and Electricity)
        panel.add(newMeasurementFactor("Energy"));

//            Communication (Mail, Phone, Internet)
        panel.add(newMeasurementFactor("Communication"));

//            Childcare
        panel.add(newMeasurementFactor("Childcare"));

//            Education
        panel.add(newMeasurementFactor("Education"));

//            Transportation
        panel.add(newMeasurementFactor("Transportation"));

//            Sex
        panel.add(newMeasurementFactor("Sex"));
        
//            Sleep
        panel.add(newMeasurementFactor("Sleep"));

//            Sewage (Excretion)
        panel.add(newMeasurementFactor("Sewage and Garbage Disposal"));

//            Money / Costs
        panel.add(newMeasurementFactor("Money"));

//            Agriculture land
        panel.add(newMeasurementFactor("Agriculture Land"));       

        return panel;
    }

    private void buildUi() {
        // Open a map centered on Cawker City, KS USA
        LatLng cawkerCity = LatLng.newInstance(39.509, -98.434);

        map = new MapWidget(cawkerCity, 2);
        map.setSize("100%", "100%");
        // Add some controls for the zoom level
        map.addControl(new LargeMapControl());

        // Add a marker
        //map.addOverlay(new Marker(cawkerCity));
        // Add an info window to highlight a point of interest
        //map.getInfoWindow().open(map.getCenter(), new InfoWindowContent("XYZe"));

        map.addMapMoveEndHandler(new MapMoveEndHandler() {

            @Override
            public void onMoveEnd(MapMoveEndEvent event) {
                updateMap();
            }
        });

        final Panel controls = getControls();

        final SplitLayoutPanel dock = new SplitLayoutPanel();
        dock.setSize("100%", "100%");
        dock.addEast(new ScrollPanel(controls), 400);
        dock.add(map);

        // Add the map to the HTML host page
        RootLayoutPanel.get().add(dock);
    }
    // /**
    // * This is the entry point method.
    // */
    // public void onModuleLoad() {
    // final Button sendButton = new Button("Send");
    // final TextBox nameField = new TextBox();
    // nameField.setText("GWT User");
    // final Label errorLabel = new Label();
    //
    // // We can add style names to widgets
    // sendButton.addStyleName("sendButton");
    //
    // // Add the nameField and sendButton to the RootPanel
    // // Use RootPanel.get() to get the entire body element
    // RootPanel.get("nameFieldContainer").add(nameField);
    // RootPanel.get("sendButtonContainer").add(sendButton);
    // RootPanel.get("errorLabelContainer").add(errorLabel);
    //
    // // Focus the cursor on the name field when the app loads
    // nameField.setFocus(true);
    // nameField.selectAll();
    //
    // // Create the popup dialog box
    // final DialogBox dialogBox = new DialogBox();
    // dialogBox.setText("Remote Procedure Call");
    // dialogBox.setAnimationEnabled(true);
    // final Button closeButton = new Button("Close");
    // // We can set the id of a widget by accessing its Element
    // closeButton.getElement().setId("closeButton");
    // final Label textToServerLabel = new Label();
    // final HTML serverResponseLabel = new HTML();
    // VerticalPanel dialogVPanel = new VerticalPanel();
    // dialogVPanel.addStyleName("dialogVPanel");
    // dialogVPanel.add(new HTML("<b>Sending name to the server:</b>"));
    // dialogVPanel.add(textToServerLabel);
    // dialogVPanel.add(new HTML("<br><b>Server replies:</b>"));
    // dialogVPanel.add(serverResponseLabel);
    // dialogVPanel.setHorizontalAlignment(VerticalPanel.ALIGN_RIGHT);
    // dialogVPanel.add(closeButton);
    // dialogBox.setWidget(dialogVPanel);
    //
    // // Add a handler to close the DialogBox
    // closeButton.addClickHandler(new ClickHandler() {
    // public void onClick(ClickEvent event) {
    // dialogBox.hide();
    // sendButton.setEnabled(true);
    // sendButton.setFocus(true);
    // }
    // });
    //
    // // Create a handler for the sendButton and nameField
    // class MyHandler implements ClickHandler, KeyUpHandler {
    // /**
    // * Fired when the user clicks on the sendButton.
    // */
    // public void onClick(ClickEvent event) {
    // sendNameToServer();
    // }
    //
    // /**
    // * Fired when the user types in the nameField.
    // */
    // public void onKeyUp(KeyUpEvent event) {
    // if (event.getNativeKeyCode() == KeyCodes.KEY_ENTER) {
    // sendNameToServer();
    // }
    // }
    //
    // /**
    // * Send the name from the nameField to the server and wait for a response.
    // */
    // private void sendNameToServer() {
    // // First, we validate the input.
    // errorLabel.setText("");
    // String textToServer = nameField.getText();
    // if (!FieldVerifier.isValidName(textToServer)) {
    // errorLabel.setText("Please enter at least four characters");
    // return;
    // }
    //
    // // Then, we send the input to the server.
    // sendButton.setEnabled(false);
    // textToServerLabel.setText(textToServer);
    // serverResponseLabel.setText("");
    // greetingService.greetServer(textToServer,
    // new AsyncCallback<String>() {
    // public void onFailure(Throwable caught) {
    // // Show the RPC error message to the user
    // dialogBox
    // .setText("Remote Procedure Call - Failure");
    // serverResponseLabel
    // .addStyleName("serverResponseLabelError");
    // serverResponseLabel.setHTML(SERVER_ERROR);
    // dialogBox.center();
    // closeButton.setFocus(true);
    // }
    //
    // public void onSuccess(String result) {
    // dialogBox.setText("Remote Procedure Call");
    // serverResponseLabel
    // .removeStyleName("serverResponseLabelError");
    // serverResponseLabel.setHTML(result);
    // dialogBox.center();
    // closeButton.setFocus(true);
    // }
    // });
    // }
    // }
    //
    // // Add a handler to send the name to the server
    // MyHandler handler = new MyHandler();
    // sendButton.addClickHandler(handler);
    // nameField.addKeyUpHandler(handler);
    // }
}
