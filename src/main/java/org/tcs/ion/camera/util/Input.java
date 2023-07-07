package org.tcs.ion.camera.util;

import java.io.Console;
import java.util.Arrays;

public class Input {
    public static UserPass getUserPass() {
        Console console = System.console();
        UserPass userPass = null;
        if (console == null) {
            Logger.msg("NO CONSOLE AVAILABLE.");
        } else {
            userPass = new UserPass(console.readLine("-- Enter ONVIF device(s) username: "), console.readPassword("-- Enter ONVIF device(s) password: "));
        }
        return userPass;
    }

    public static class UserPass {
        public String username;
        public String password;

        public UserPass(String username, char[] password) {
            this.username = username;
            this.password = Arrays.toString(password);
        }
    }
}
