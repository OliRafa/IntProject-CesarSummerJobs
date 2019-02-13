import os

#Needs to be root path
root = "/home/summer/Documents/GitHub/SmartCapivara/smart-capivara"

def criar_pasta_em_dataset(nome, folder_name):
	path = root + '/' + folder_name + '/' + nome
	command_line = "mkdir " + path
	os.system(command_line)
	print("Pasta " + nome + " criada!\n")
	return [path, True]

'''
command = "pwd"
a = os.system("ls -la")
print(a)

'''
dataset = ''
can_create = False

print("-----List all files from directory-----\n")
for filename in os.listdir(root):
	print(filename)
	if filename == 'dataset':
		dataset = 'dataset'
		can_create = True

if can_create == True:
	[pasta_endereco,pasta_criada] = criar_pasta_em_dataset("novo_usuario_2", dataset)

	if pasta_criada == True:
	alimentar_banco()
