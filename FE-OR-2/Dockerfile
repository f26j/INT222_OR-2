# Build stage
FROM node:lts-alpine AS BUILD_STAGE
COPY package*.json ./
RUN npm install
COPY . .
RUN npm run build

# Production stage
FROM nginx:stable-alpine AS PRODUCTION_STAGE
COPY --from=BUILD_STAGE /dist /usr/share/nginx/html
EXPOSE 80
CMD ["nginx", "-g", "daemon off;"]
