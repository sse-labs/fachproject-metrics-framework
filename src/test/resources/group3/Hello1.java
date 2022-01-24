public class hello1 {

    String name = "Ann";
    int age = 25;
    int address = 44;

    public hello1(String name, int age, int address){

        this.name = name;
        this.age = age;
        this.address = address;

    }

    public static String myName(){

        hello1 hello2 = new hello1("rita", 3, 0);
        System.out.println (hello2.name);
        return "4";
    }


    public static int myAge(){

        hello1 hello3 = new hello1("rito", 3, 0);
        System.out.println (hello3.age);
        return 9;
    }

    // // 2te field -> direct connection
    public static void main (String[] args){

        hello1 hello = new hello1("Tony", 20, 33);

        System.out.println (hello.age);
        System.out.println (hello.name);
        System.out.println (hello.address);


    }

    // total direct connection pairs: nd = 3
    // number of possible connections = 5*4/2 = 10
    // 3/10 = 0.3
}
