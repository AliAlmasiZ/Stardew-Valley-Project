#!/bin/bash

# ANSI
RED='\033[0;31m'
GREEN='\033[0;32m'
BLUE='\033[0;34m'
CYAN='\033[0;36m'
MAGENTA='\033[0;35m'
YELLOW='\033[1;33m'
BOLD='\033[1m'
RESET='\033[0m'

if [ "$#" -ne 1 ]; then
  echo -e "${YELLOW}Usage:${RESET} $0 <num_clients>"
  exit 1
fi

num_clients=$1

echo -e "${MAGENTA}Starting Server...${RESET}"
./gradlew server:run > "server.log" 2>&1 &
sleep 2


for (( i=1; i<=num_clients; i++ )); do
  echo -e "${CYAN}Starting client #${i}${RESET}"
  ./gradlew lwjgl3:run > "client${i}.log" 2>&1 &
  sleep 5
done

wait

echo -e "${GREEN}All ${num_clients} clients have finished.${RESET}"
