package com.update_stands;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Date;


import com.ConnectionSsh;

public class UpdateStands {

    String node1 = null;
    String node2 = null;
    String port = null;
    int type;

    // Конструктор с инициализацией переменных
    public UpdateStands() {

        try {
            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(System.in));
            System.out.println("Выберите тип стенда: ");
            System.out.println("1. Одна нода\n2. Две ноды");
            this.type = Integer.parseInt(reader.readLine());
            if (type == 1) {
                System.out.println("Введите адрес ноды: ");
                this.node1 = reader.readLine();
            } else if (type == 2) {
                System.out.println("Введите адрес первой ноды: ");
                this.node1 = reader.readLine();
                System.out.println("Введите адрес второй ноды: ");
                this.node2 = reader.readLine();
            }
            System.out.println("Введите порт: ");
            this.port = reader.readLine();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //точка входа
    public void runUpdate() {
        //переопределяем метод в зависимости от кол-ва нод
        if (type == 1) update(node1, port);
        else if (type == 2) update(node1, node2, port);

    }

    private void update(String node1, String port) {
        String[] result;

        ConnectionSsh connectionSsh = new ConnectionSsh(node1);


    }

    private void update(String node1, String node2, String port) {
        String command = "";
        String[] result;

        ConnectionSsh connectionSsh = new ConnectionSsh(node1);

        result = connectionSsh.runCommand(command);
        System.out.println("Код:" + result[0]);
        System.out.println("Вывод: " + result[1]);

    }
    private void sufd_stand(String node1, String url, String path, String owner){
        String[] result;
        ConnectionSsh connectionSsh = new ConnectionSsh(node1);
        System.out.println("Бекапим ядро...");
        result = connectionSsh.runCommand("bash << EOF" +
                "\nsudo su - "+ owner +
                "\ncd "+ path +"/webapps"+
                "\nrename .war .war.bk *.war"+
                "\nEOF");
        System.out.println("OK");
        System.out.println("Скачиваем ядро...");
        result = connectionSsh.runCommand("bash << EOF" +
                "\nsudo su - "+ owner +
                "\ncd "+ path +"/webapps"+
                "\nwget --no-check-certificate "+ url +
                "\nEOF");
        System.out.println("OK");
        System.out.println("Ядро установлено");
    }

    private void sufd_stand_patch(String node1, String url, String path, String owner){


    }

    private void sufd_libs(String node1, String url, String path, String owner){

        String[] result;
        ConnectionSsh connectionSsh = new ConnectionSsh(node1);
        System.out.println("Скачиваем либы в tmp и проверяем содержимое архива");
        result = connectionSsh.runCommand("bash << EOF" +
                "\n sudo su - "+ owner +
                "\nmkdir /tmp/showlibs/"+
                "\ncd /tmp/showlibs/"+
                "\nwget --no-check-certificate "+ url +
                "\nunzip *.zip"+
                "\nEOF");
        System.out.println("Код: " + result[0]);
        System.out.println("Вывод: " + result[1]);
        result = connectionSsh.runCommand("ls /tmp/showlibs/");
        System.out.println("Проверяем что распаковалось...");
        System.out.println("Код: " + result[0]);
        System.out.println("Вывод:\n" + result[1]);
        System.out.println("Это похоже на sufd ?");
        try {
            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(System.in));
            Date date = new Date();
            SimpleDateFormat formatter = new SimpleDateFormat("ddMMyyyy");
            System.out.println("1.Да\n2.Нет:(");
            String input = reader.readLine();
            if(input == "1"){
                System.out.println("Продолжаем...");
                System.out.println("Бекапим старые либы...");
                result = connectionSsh.runCommand("mv "+ path +"/lib/ext/sufd "+
                        path + "/lib/ext/sufd_"+formatter.format(date));
                if(result[0] == "0"){
                    System.out.println("OK");
                } else{
                    System.out.println("Что-то пошло не так");
                    return;
                }
                System.out.println("Переносим либы в папку стенда");
                result = connectionSsh.runCommand("mv /tmp/showlibs/sufd "+ path +"/lib/ext/");
                if(result[0] == "0"){
                    System.out.println("OK");
                } else{
                    System.out.println("Что-то пошло не так");
                    return;
                }
                System.out.println("Удаляем временные файлы...");
                result = connectionSsh.runCommand("rm -f /tmp/showlibs");
                if(result[0] == "0"){
                    System.out.println("OK");
                } else{
                    System.out.println("Что-то пошло не так");
                    return;
                }

            }else{
                System.out.println("Прерывание...");
                System.out.println("Удаляем временные файлы...");
                result = connectionSsh.runCommand("rm -f /tmp/showlibs");
                if(result[0] == "0"){
                    System.out.println("OK");
                } else{
                    System.out.println("Что-то пошло не так");
                    return;
                }
                return;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
