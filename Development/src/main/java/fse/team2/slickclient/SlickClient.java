package fse.team2.slickclient;

import java.io.InputStreamReader;

import fse.team2.slickclient.commands.AbstractCommand;
import fse.team2.slickclient.controller.CommandControllerImpl;
import fse.team2.slickclient.model.ClientUserImpl;
import fse.team2.slickclient.services.HttpServiceImpl;
import fse.team2.slickclient.services.MessageServiceImpl;
import fse.team2.slickclient.services.UserServiceImpl;
import fse.team2.slickclient.view.CommandView;
import fse.team2.slickclient.view.CommandViewImpl;

public class SlickClient {
    public static void main(String[] args) {
        StringBuilder log = new StringBuilder();
        CommandView commandView = new CommandViewImpl();
        AbstractCommand abstractCommand = new CommandControllerImpl(new InputStreamReader(System.in), commandView);
        if (args.length == 0 || !args[0].equals("test")) {
            abstractCommand.apply(new ClientUserImpl(log, UserServiceImpl.getInstance(HttpServiceImpl.getInstance()), MessageServiceImpl.getInstance(HttpServiceImpl.getInstance())));
        }
    }
}
