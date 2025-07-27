package main

import (
	"bytes"
	"context"
	"fmt"
	"io"
	"net/http"
	"time"

	"github.com/redis/go-redis/v9"
)

var (
	backends = []string{
		"http://api01:8080/payments",
		"http://api02:8080/payments",
	}
	counter = 0
)

func main() {
	ctx := context.Background()
	rdb := redis.NewClient(&redis.Options{
		Addr: "redis:6379",
	})

	for {
		result, err := rdb.RPop(ctx, "payments_queue").Result()
		if err == redis.Nil {
			time.Sleep(100 * time.Millisecond)
			continue
		} else if err != nil {
			fmt.Println("Redis error:", err)
			continue
		}

		target := backends[counter%len(backends)]
		counter++

		resp, err := http.Post(target, "application/json", bytes.NewBuffer([]byte(result)))
		if err != nil {
			fmt.Println("Failed to send to", target, ":", err)
			continue
		}

		io.Copy(io.Discard, resp.Body)
		resp.Body.Close()

		fmt.Println("Sent to", target)
	}
}
