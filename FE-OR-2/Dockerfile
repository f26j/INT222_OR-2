# Build stage
FROM node:20-alpine AS BUILD_STAGE
WORKDIR /app
COPY package*.json ./
RUN npm install
COPY . .
RUN npm run build

# Production stage
FROM nginx:1.26-alpine AS PRODUCTION_STAGE
COPY --from=BUILD_STAGE /app/dist /usr/share/nginx/html
COPY ./nginx/default.conf /etc/nginx/conf.d/default.conf
EXPOSE 80
CMD ["nginx" "-g" "daemon off;"]
