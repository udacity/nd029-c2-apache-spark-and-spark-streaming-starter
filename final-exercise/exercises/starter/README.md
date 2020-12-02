# Decode and Join two Base64 Encoded JSON DataFrames

Now that you have learned how to use `unbase64` to extract useful data, let's take things a step further. There is often a need to join data from separate data types. In this exercise we join data from the `customer` and the `customerLocation` message types.

- If you don't already have the docker containers running, execute the following commands:

    ```
    cd [repositoryfolder]
    docker-compose up
    ```

- You should see 9 containers when you run this command:
    ```
    docker ps
    ```

- Complete the current-country.py python script
- Submit the application to the spark cluster:
     - From the terminal, cd to the lesson-3-redis-base64-json/exercises/starter subfolder of the repository, then type: 

    Mac/Linux/Unix:
     ```
     submit-current-country.sh
     ```

    Windows:
     ```
     submit-current-country.cmd
     ```   
- Watch the terminal for the values to scroll past 