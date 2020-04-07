#!/bin/bash
path=$1
> $path/../out/Player.java
grep -Eh 'import' $path/*.java > $path/../out/Player.java
grep -vh 'import' $path/*.java | grep -v 'package' >> $path/../out/Player.java
