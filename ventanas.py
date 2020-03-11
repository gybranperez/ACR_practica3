from socket import AF_INET, socket, SOCK_STREAM,SOCK_DGRAM

import tkinter as tk
from tkinter import *
from tkinter import ttk 
from tkinter import messagebox

#Para la funcion sleep
import time
#Para comparar el puerto
import math
#Para la validacion de expresiones regulares
import re
import sys

PORT = 33000
HOST = "127.0.0.1"

def getHost():
    return str(HOST)

def getPort():
    return str(PORT)

def castEntero(entryWidget):
    value = str(entryWidget)
    try:
        return int(value)
    except ValueError:
        return None

def obtenerIpLocal(): 
    s = socket(AF_INET, SOCK_DGRAM)
    try:
        # doesn't even have to be reachable
        s.connect(('10.255.255.255', 1))
        IP = s.getsockname()[0]
    except:
        IP = '127.0.0.1'
    finally:
        s.close()
    return IP



class DatosConexion():
    def __init__(self,titulo="Puerto",pregunta="Ingrese "):
        HOST = obtenerIpLocal()
        #Instanciamos un objeto de ventana
        self.root = tk.Tk()
        #Aniadimos el titulo
        self.root.title(str(titulo))
        #Asignamos tamanio
        self.root.geometry('500x150')
        #El tamanio no sera modificable
        self.root.resizable(width=0, height=0)
        #Ponemos etiqueta antes del input



        self.lblIP = tk.Label(self.root, text=str(pregunta+"direccion IP: "))
        self.lblIP.grid(column=0, row=0)
        #state='disabled'
        self.inputIP = tk.Entry(self.root,width=20,font = ('Helvetica', 15, 'bold'),bg='#19303d')
        self.inputIP.delete(0,tk.END)
        self.inputIP.insert(0,HOST)
        self.inputIP.grid(column=1, row=0)
        

        self.lblPuerto = tk.Label(self.root, text=str(pregunta+"puerto: "))
        self.lblPuerto.grid(column=0, row=1)
        self.inputPuerto = tk.Entry(self.root,width=20,font = ('Helvetica', 15, 'bold'),bg='#19303d')
        self.inputPuerto.focus_force()
        self.inputPuerto.delete(0,tk.END)
        self.inputPuerto.insert(0,PORT)
        self.inputPuerto.grid(column=1, row=1)
        
        self.mensajeError = tk.StringVar()
        self.mensajeError.set("ALV")
        self.lblError = tk.Label(self.root, textvariable=self.mensajeError, font = ('Helvetica', 15, 'bold'),bg='#fff', fg='red')
        self.lblError.grid(column=0, row=2)
        self.btnValor = tk.Button(
                            self.root, text = 'Enviar', 
                            command=self.mostrarDatos, 
                            bg='#0052cc', fg='#ffffff')
        #self.btnValor['font'] = fontStyle  
        self.btnValor.grid(column=1, row=2)


        self.root.protocol("WM_DELETE_WINDOW", self.ask_quit)

        self.root.mainloop()
    
    def ask_quit(self):
        if messagebox.askokcancel("Quit", "You want to quit now? *sniff*"):
            self.root.destroy()

    def obtenerPuerto(self):
    # VALIDACIONES DE PUERTO
        #Verificamos que el puerto este dentro de el rango valido
        conversion = castEntero(self.inputPuerto.get())
        global PORT
        if conversion is not None:
            if 1000 <= conversion <= 60000:
                # get() - Metodo utilizado para obtener el valor de un input
                PORT = str(self.inputPuerto.get())
            # destroy() - Metodo de clase para cerrar la ventana de Tkinter 
        else:
            print(str(PORT) + " > Esta mal")
            self.lblError = tk.StringVar()
            self.lblError.set("El puerto no es valido")
        return str(PORT)
    

    def obtenerIP(self):
        ipingresada = self.inputIP.get()
        resulta = re.match('^(?:[0-9]{1,3}\.){3}[0-9]{1,3}$', str(ipingresada))
        global HOST
        if resulta:
            HOST = str(ipingresada)
        else:
            print(HOST + "Esta mal")
            self.lblError = tk.StringVar()
            self.lblError.set("La IP no es valida")
        return HOST


    def mostrarDatos(self):
        print(str(self.obtenerIP()) + ":" + str(self.obtenerPuerto()))

    def cerrarVentana(self):
        self.root.destroy()


"""
app.minsize(width=600, height=400)
app.maxsize(width=600, height=400)
"""