package apps;

import proto.App;

public class AppPool {
    public static void main(String[] args){
        App app = App.newBuilder().setId(1).setName("test-app").build();
    }
}
