public class empty {


    public static String myName(){

        System.out.println(myAge());
        return "Tony";
    }

    //  no direct connection
    public static int myAge(){

        return 9;
    }

    public static void main (String[] args){

        System.out.println (myAge());


    }

    public void test (){

        System.out.println("no return");
    }

}