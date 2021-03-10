FROM node:current-alpine3.10

WORKDIR /app

COPY ./package*.json ./

RUN npm install

EXPOSE 8080

CMD ["npm", "run", "start"]