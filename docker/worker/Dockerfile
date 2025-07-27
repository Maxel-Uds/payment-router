FROM golang:1.22 as builder

WORKDIR /app
COPY worker.go .

ENV CGO_ENABLED=0
RUN go mod init worker
RUN go mod tidy
RUN go build -o worker worker.go

FROM debian:bullseye-slim

WORKDIR /app
COPY --from=builder /app/worker .

CMD ["./worker"]