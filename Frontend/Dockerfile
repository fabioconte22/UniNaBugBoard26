# 1) Build di produzione dell'app Angular, con la stessa versione di Node dello sviluppo (.nvmrc).
FROM node:24-alpine AS build
WORKDIR /src

COPY package.json package-lock.json ./
RUN npm ci
COPY . .

ENV NG_CLI_ANALYTICS=false
RUN npx ng build

# 2) nginx serve i file statici e inoltra /api e /uploads al gateway.
FROM nginx:1.27-alpine
COPY nginx.conf /etc/nginx/conf.d/default.conf
COPY --from=build /src/dist/bugboard26-frontend/browser /usr/share/nginx/html
