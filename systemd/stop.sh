#!/bin/sh
kill -KILL `ps ax | grep [m]t2.jar | awk '{print $1}'`
