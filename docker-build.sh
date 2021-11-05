#! /bin/bash
sbt "scalafmtAll; clean; Docker/publishLocal"
