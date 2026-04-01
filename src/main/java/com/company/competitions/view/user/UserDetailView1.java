package com.company.competitions.view.user;

import com.company.competitions.entity.User;
import com.company.competitions.view.main.MainView;
import com.vaadin.flow.router.Route;
import io.jmix.flowui.view.EditedEntityContainer;
import io.jmix.flowui.view.StandardDetailView;
import io.jmix.flowui.view.ViewController;
import io.jmix.flowui.view.ViewDescriptor;

@Route(value = "users1/:id", layout = MainView.class)
@ViewController(id = "User.detail1")
@ViewDescriptor(path = "user-detail-view1.xml")
@EditedEntityContainer("userDc")
public class UserDetailView1 extends StandardDetailView<User> {
}