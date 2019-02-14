import os


def create_folder(ROOT_PATH, _id):
    dataset = False
    create = True
    path = 0

    for filename in os.listdir(ROOT_PATH):
        if filename == 'dataset': dataset = True

    if not dataset:
        os.system("mkdir dataset")
        DATASET_PATH = ROOT_PATH + '/dataset'

    for foldername in os.listdir(DATASET_PATH):
        if foldername is _id:
            create = False
            path = DATASET_PATH + '/' + foldername

    if create:
        os.system("cd "+ DATASET_PATH +" && mkdir " + _id)
        path = DATASET_PATH + '/' + _id 

    return path