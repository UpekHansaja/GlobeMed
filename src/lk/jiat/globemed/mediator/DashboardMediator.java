package lk.jiat.globemed.mediator;

import java.awt.Component;

public interface DashboardMediator {

    void notify(Component sender, String event, Object data);

    void registerComponent(String name, Component component);

    void unregisterComponent(String name);
}
