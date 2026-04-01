package com.company.competitions.view.position;

import com.company.competitions.entity.Position;
import com.company.competitions.view.main.MainView;
import com.vaadin.flow.router.Route;
import io.jmix.flowui.view.*;


@Route(value = "positions", layout = MainView.class)
@ViewController(id = "Position_.list")
@ViewDescriptor(path = "position-list-view.xml")
@LookupComponent("positionsDataGrid")
@DialogMode(width = "64em")
public class PositionListView extends StandardListView<Position> {
}