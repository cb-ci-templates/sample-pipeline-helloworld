#! /bin/bash

set -e # option instructs bash to immediately exit if any command [1] has a non-zero exit status
set -u # Affects variables. When set, a reference to any variable you haven't previously defined - with the exceptions of `$*` and `$@` - is an error, and causes the program to immediately exit.
set -o pipefail # This setting prevents errors in a pipeline from being masked. If any command in a pipeline fails, that return code will be used as the return code of the whole pipeline.
set -x # Enables a mode of the shell where all executed commands are printed to the terminal.

RESULT_JSON=mytest.json
if [ -e $RESULT_JSON ];then echo "clean" && rm $RESULT_JSON;fi
greetings="World"
echo "Hello $greetings"

#This will fail fast because of "set -u"
#script exit  with error code !=0
echo $notExist
#This will not be executed because script fails before
echo "Hello $greetings"


