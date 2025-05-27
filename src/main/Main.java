package main;

import java.util.ArrayList;
import java.util.Random;

class Book {
	private final String name;
	private boolean lended;

	public Book(final String name) {
		this.name = name;
		this.lended = false;
	}

	public synchronized final String getName() {
		return this.name;
	}

	public synchronized final boolean isLended() {
		return this.lended;
	}

	public synchronized void lendBook() {
		while (this.isLended()) {
			try {
				wait();
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
			}
		}

		this.lended = true;
		notifyAll();
	}

	public synchronized void returnBook() {
		if (!this.isLended()) {
			throw new RuntimeException("Livro " + this.name + " foi retornado sem estar emprestado.");
		}

		this.lended = false;
		notifyAll();
	}

}

class User extends Thread {
	private final String name;
	private ArrayList<Book> books;
	private Random rng;

	public User(final String name, ArrayList<Book> books) {
		this.name = name;
		this.books = books;
		this.rng = new Random();
	}

	@Override
	public void run() {
		Book selectedBook = books.get(rng.nextInt(0, books.size()));

		if (selectedBook.isLended()) {
			System.out.println(this.name + " está esperando o livro " + selectedBook.getName() + " ficar disponível");
		}

		selectedBook.lendBook();
		System.out.println(this.name + " emprestou o livro " + selectedBook.getName());

		try {
			Thread.sleep(rng.nextInt(1000, 2001));
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
		}

		System.out.println(this.name + " devolveu o livro " + selectedBook.getName());
		selectedBook.returnBook();

	}
}

public class Main {

	public static void main(String[] args) {
		ArrayList<User> users = new ArrayList<>();
		ArrayList<Book> books = new ArrayList<>();

		for (int i = 1; i <= 10; i++) {
			users.add(new User("Usuário " + i, books));
		}

		for (int i = 1; i <= 3; i++) {
			books.add(new Book(String.valueOf(i)));
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
