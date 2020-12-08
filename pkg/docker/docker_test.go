package docker

import (
	"fmt"
	"log"
	"os"
	"path/filepath"
	"testing"

	"github.com/stretchr/testify/assert"
)

func TestGetImageSource(t *testing.T) {

	cases := []struct {
		imageName   string
		registryURL string
		localPath   string
		want        string
	}{
		{"imageName", "", "", "imageName"},
		{"imageName", "", "localPath", "daemon://localPath"},
		{"imageName", "http://registryURL", "", "remote://registryURL/imageName"},
		{"imageName", "https://containerRegistryUrl", "", "remote://containerRegistryUrl/imageName"},
		{"imageName", "registryURL", "", "remote://registryURL/imageName"},
	}

	client := Client{}

	for _, c := range cases {

		options := ClientOptions{ImageName: c.imageName, RegistryURL: c.registryURL, LocalPath: c.localPath}
		client.SetOptions(options)

		got, err := client.GetImageSource()

		assert.Nil(t, err)
		assert.Equal(t, c.want, got)
	}
}

func TestDownloadImageToPath(t *testing.T) {
	client := Client{}

	options := ClientOptions{ImageName: "maven:3.6.3-jdk-8-slim", RegistryURL: "", LocalPath: ""}
	client.SetOptions(options)

	imageSource, _ := client.GetImageSource()

	image, err := client.DownloadImageToPath(imageSource, ".")
	if err != nil {
		log.Fatalf("failed to download docker image: %v", err)
	}

	fmt.Println("v1 image:", image)

	fileName := "maven3.tar"
	tarFilePath := filepath.Join(".", fileName)
	tarFile, err := os.Create(tarFilePath)
	err = client.TarImage(tarFile, image)

	assert.NoError(t, err)
}
