package com.company.competitions.view.position;

import com.company.competitions.entity.Position;
import com.company.competitions.view.main.MainView;
import com.vaadin.flow.router.Route;
import io.jmix.flowui.view.EditedEntityContainer;
import io.jmix.flowui.view.StandardDetailView;
import io.jmix.flowui.view.ViewController;
import io.jmix.flowui.view.ViewDescriptor;

@Route(value = "positions/:id", layout = MainView.class)
@ViewController(id = "Position_.detail")
@ViewDescriptor(path = "position-detail-view.xml")
@EditedEntityContainer("positionDc")
public class PositionDetailView extends StandardDetailView<Position> {
}