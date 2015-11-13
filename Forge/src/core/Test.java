package core;

public class Test {

    public static void main(String[] args) {
        Signal<String> words = new Signal();
        words.set("hi");
        words.set("hi");
        words.set("hi");
        words.set("hi");
        words.set("hi");
        Signal<Integer> count = words.count();
        words.set("hi");
        words.set("hi");
        words.set("hi");
        words.set("hi");
        words.set("hi");
        words.set("hi");
        words.set("hi");
        words.set("hi");
        words.set("hi");
        words.set("hi");
        System.out.println(count.get());
    }
}
