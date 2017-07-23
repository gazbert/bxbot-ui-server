# BX-bot UI Server

[![Build Status](https://travis-ci.org/gazbert/bxbot-ui-server.svg?branch=master)](https://travis-ci.org/gazbert/bxbot-ui-server)

**Note:** This project is very much work in progress and not safe for production.

## What is BX-bot UI Server?
A Spring Boot backend for use with [BX-bot UI](https://github.com/gazbert/bxbot-ui.git). 
It forwards configuration updates and commands from BX-bot UI to [BX-bots](https://github.com/gazbert/bxbot.git) running in the cloud. 

## Architecture
![bxbot-ui-server-architecture.png](./docs/bxbot-ui-server-architecture.png)

- **[BX-bot - (n)]((https://github.com/gazbert/bxbot.git))** - multiple BX-bot instances running in the cloud. 
  Each bot integrates with 1 Exchange. The bot provides a secured REST API for BX-bot UI Server to consume.
- **BX-bot UI Server** - provides a secured ([JWT](https://jwt.io/) over HTTPS) REST API for BX-bot UI to consume. 
  It proxies configuration updates and commands to the BX-bots.
- **[BX-bot UI](https://github.com/gazbert/bxbot-ui.git)** - an admin interface for sending configuration updates and 
  commands to BX-bots running in the cloud. It consumes BX-bot UI Server's REST API.
 
## Credits
This app was made possible thanks to the following:

* An excellent [sample app](https://github.com/szerhusenBC/jwt-spring-security-demo)
  demonstrating JWT Authentication with Spring Boot by Stephan Zerhusen.       
