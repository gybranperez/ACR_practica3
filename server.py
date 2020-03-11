#!/usr/bin/env python3

"""
Practica 3 - ACR
Servidor para una aplicacion de chat con multihilos asincrono
"""
#import socket
from socket import AF_INET, socket, SOCK_STREAM,SOCK_DGRAM
#from socket import *
from ventanas import getHost, getPort
from ventanas import DatosConexion
#Para atender a los clientes abrimos hilos
from threading import Thread
#Para las ventanas de la interfaz
import tkinter as tk
from tkinter import ttk 
#Para la funcion sleep
import time
#Para comparar el puerto
import math
cliente=0
HOST = "127.0.0.1"
PORT = 33000


def aceptaConexionesEntrantes():
    """Sets up handling for incoming diccionarioClientes."""
    print("Tu direccion es " + str(HOST) + ":" + str(PORT))
    while True:
        cliente, direccionCliente = SERVER.accept()
        print("Se ha connectado %s:%s." % direccionCliente)
        cliente.send(bytes("Saludos! Ingresa tu nickname...", "utf8"))
        direccionesActivas[cliente] = direccionCliente
        Thread(target=atenderCliente, args=(cliente,)).start()
    terminar = input("Desea terminar el server: 1=Si")

def atenderCliente(cliente):  # Takes cliente socket as argument.
    """Atiende UNA conexion de un cliente."""
    nombre = cliente.recv(BUFSIZ).decode("utf8")
    welcome = 'Hola %s! cuando quieras salir solo escribe {quit} y listo.' % nombre
    cliente.send(bytes(welcome, "utf8"))
    msg = "%s se unio al chat!" % nombre
    broadcast(bytes(msg, "utf8"))
    diccionarioClientes[cliente] = nombre

    while True:
        msg = cliente.recv(BUFSIZ)
        if msg != bytes("{quit}", "utf8"):
            broadcast(msg, nombre+": ")
        else:
            cliente.send(bytes("{quit}", "utf8"))
            cliente.close()
            del diccionarioClientes[cliente]
            broadcast(bytes("%s has left the chat." % nombre, "utf8"))
            break


def broadcast(msg, prefix=""):  # prefix is for nombre identification.
    """Envia el mensaje Broadcasta todos dentro del diccionarioClientes."""
    for sock in diccionarioClientes:
        sock.send(bytes(prefix, "utf8")+msg)

diccionarioClientes = {}
direccionesActivas = {}
#   Obteniendo direccion IP local para el servidor

#   Obteniendo puerto que se va a utilizar para la comunicacion
#   Se crea una ventana que pide el puerto
ventanaPuerto = DatosConexion()
HOST = getHost()
PORT = int(getPort())


#   Tamanio del buffer
BUFSIZ = 1024
#   Direccion en donde sirve
ADDR = (HOST, PORT)
#   Instancia de socket y enlazado
SERVER = socket(AF_INET, SOCK_STREAM)
SERVER.bind(ADDR)




if __name__ == "__main__":
    SERVER.listen(5)
    print("Waiting for connection...")
    ACCEPT_THREAD = Thread(target=aceptaConexionesEntrantes)
    ACCEPT_THREAD.start()
    ACCEPT_THREAD.join()
    
    SERVER.close()
