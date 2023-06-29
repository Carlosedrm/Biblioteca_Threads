import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.Semaphore;

class Biblioteca extends Semaphore {
    private ArrayList<Integer> livrosDisponiveis = new ArrayList<>();

    public Biblioteca() {
        super(1);
        for (int i = 1; i < 11; i++) {
            livrosDisponiveis.add(i);
        }
    }

    public int Emprestar(int id_usuario) throws InterruptedException {
        try {
            Random rand = new Random();
            int livro = rand.nextInt(10 - 1) + 1;
            synchronized (this) {
                while (true) {
                    if (livrosDisponiveis.contains(livro)) {
                        livrosDisponiveis.remove(Integer.valueOf(livro));
                        System.out.println("Usuario " + id_usuario + " emprestou o livro: " + livro);
                        return livro;
                    } else {
                        System.out.println("Usuario " + id_usuario + " aguardando livro " + livro + " ficar disponivel.");
                        wait();
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    public void Devolver(int id_usuario, int livro) {
        synchronized (this) {
            livrosDisponiveis.add(livro);
            System.out.println("Usuario " + id_usuario + " devolveu o livro: " + livro);
            notify();
        }
    }
}

class Usuario extends Thread {
    private Biblioteca biblioteca;
    private int id_usuario;

    public Usuario(Biblioteca b, int id) {
        biblioteca = b;
        id_usuario = id;
    }

    public void esperar() {
        Random rand = new Random();
        try {
            int tempo = rand.nextInt(2001) + 1000;
            Thread.sleep(tempo);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        try {
            System.out.println("Usuario " + id_usuario + " Iniciado.");
            while (true) {
                // Pega um livro emprestado
                int livro = biblioteca.Emprestar(id_usuario);

                // Espera alguns segundos
                esperar();

                // Devolve o livro
                biblioteca.Devolver(id_usuario, livro);

                // Espera alguns segundos
                esperar();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}

public class TDE {
    public static void main(String[] args) throws InterruptedException {
        // A biblioteca
        Biblioteca biblioteca = new Biblioteca();

        // Um arraylist que contém todos os usuários
        ArrayList<Usuario> usuarios = new ArrayList<>();

        for (int i = 0; i < 3; i++) {
            Usuario user = new Usuario(biblioteca, i + 1);
            usuarios.add(user);
            user.start();
        }

        for (int i = 0; i < 3; i++) {
            usuarios.get(i).join();
        }

    }
}
