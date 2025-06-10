package tdeperformance;

import java.util.ArrayList;
import java.util.Random;

class Book {
    private int id;
    private boolean lended;

    public Book(int id) {
        this.id = id;
        this.lended = false;
    }

    public boolean isLended() {
        return lended;
    }

    public void lend() {
        lended = true;
    }

    public void handBack() {
        lended = false;
    }

    public int getId() {
        return id;
    }

}

class User extends Thread {
    private final int id;
    private final ArrayList<Book> books;
    private final Random rand;

    public User(int id, ArrayList<Book> books) {
        this.id = id;
        this.books = books;
        this.rand = new Random();
    }

    @Override
    public void run() {
        try {
            while (true) {
                int randomBookIndex = rand.nextInt(books.size());
                Book book = books.get(randomBookIndex);

                synchronized (book) {
                    while (book.isLended()) {
                        System.out
                                .println("Usuário " + id + " - esperando livro " + book.getId() + " ficar disponível");
                        book.wait();
                    }

                    book.lend();
                    System.out.println("Usuário " + id + " - emprestou o livro " + book.getId());
                }

                Thread.sleep(rand.nextInt(1000, 2001));

                synchronized (book) {
                    book.handBack();
                    System.out.println("Usuário " + id + " - devolveu o livro " + book.getId());
                    book.notifyAll();
                }

                Thread.sleep(rand.nextInt(1000, 2001));
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}

public class Main {
    public static void main(String[] args) {
        ArrayList<Book> books = new ArrayList<>();
        ArrayList<User> users = new ArrayList<>();

        for (int i = 1; i <= 10; i++) {
            books.add(new Book(i));
        }

        for (int i = 1; i <= 3; i++) {
            users.add(new User(i, books));
        }

        for (User user : users) {
            user.start();
        }

        for (User user : users) {
            try {
                user.join();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }
}
