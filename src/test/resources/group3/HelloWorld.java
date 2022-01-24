
public class helloWorld {


     String name = "Ann";
     int age = 25;
     int address = 44;

    public helloWorld(String name, int age, int address){

        this.name = name;
        this.age = age;
        this.address = address;

    }

    // direct connect with myAge nd = 1

    public static String myName(){

        System.out.println(myAge());
        return "Tony";
    }

    //  no direct connection
    public static int myAge(){
      helloWorld hello1 = new helloWorld("Tony", 20, 5);
      System.out.println (hello1.age);   // 1st field -> direct connection

        return 9;
    }

    // // 2te field -> direct connection
    public static void main (String[] args){

        helloWorld hello = new helloWorld("Tony", 20, 33);

        System.out.println (hello.age);
        System.out.println (hello.name);
        System.out.println (hello.address);

    }

    // total direct connection pairs: nd = 3
    // number of possible connections = 4*3/2 = 6
    // 3/6 = 0.5


}
