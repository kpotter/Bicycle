package cyclist.view.mediator;

import javafx.event.EventHandler;

import org.apache.log4j.Logger;
import org.puremvc.java.multicore.patterns.mediator.Mediator;
import org.puremvc.java.multicore.patterns.observer.Notification;

import cyclist.CyclistNames;
import cyclist.controller.ApplicationConstants;
import cyclist.model.vo.ToolInfo;
import cyclist.view.component.View;
import cyclist.view.component.Workspace;
import cyclist.view.event.CyclistDropEvent;

public class WorkspaceMediator extends Mediator {
	
	static Logger log = Logger.getLogger(WorkspaceMediator.class);
	
	public WorkspaceMediator(Workspace view) {
		super(CyclistNames.WORKSPACE_MEDIATOR, view);
		
		init();
	}
	
	@Override
	public Workspace getViewComponent() {
		return (Workspace) super.getViewComponent();
	}
	
	private Workspace workspace() {
		return (Workspace) super.getViewComponent();
	}
	
	private void init() {
		workspace().setOnToolDrop( new EventHandler<CyclistDropEvent>() {
			public void handle(CyclistDropEvent event) {
				// TODO: move to a Command(?)
				try {
					ToolInfo info = event.getInfo();
					Class<?> c = Class.forName(info.view);
					View view = (View) c.newInstance();
					view.setTranslateX(event.getX());
					view.setTranslateY(event.getY());
					view.setParam(info.viewParam);
					workspace().addView(view);
					
					if (event.getInfo().mediator != null) {
						Class<?> m = Class.forName(event.getInfo().mediator);
						CyclistMediator mediator = (CyclistMediator) m.newInstance();
						mediator.setParam(info.mediatorParam);
						getFacade().registerMediator(mediator);
					
						mediator.setViewComponent(view);
					
						mediator.handleNotification(new Notification(ApplicationConstants.MEDIATOR_INIT));
					}
				} catch (Exception e) {
					log.error("Error while creating Tool and Mediator", e);
				}
			}
		});
	}
}
