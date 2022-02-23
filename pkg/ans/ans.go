package ans

import (
	"bytes"
	"github.com/SAP/jenkins-library/cmd"
	"github.com/SAP/jenkins-library/pkg/cpi"
	piperhttp "github.com/SAP/jenkins-library/pkg/http"
	"github.com/SAP/jenkins-library/pkg/log"
	"net/http"
)

type ANS struct {
	ansClient piperhttp.API
}

const body = `{
"eventType": "NotWorkingExternalRecommendationService",
"eventTimestamp": 1535618178,
"resource": {
"resourceName": "web-shop",
"resourceType": "app",
"tags": {
"env": "prod"
}
},
"severity": "FATAL",
"category": "ALERT",
"subject": "Overloaded external dependency of My Web Shop external dependency",
"body": "External dependency showing recommendations does not respond on time. Stop some clients to reduce the load.",
"tags": {
"ans:correlationId": "30118",
"ans:status": "CREATE_OR_UPDATE",
"customTag": "42"
}
}`

func (ans *ANS) Send() error {
	serviceKey, err := cpi.ReadANSServiceKey(cmd.GeneralConfig.ANSServiceKey)
	ans.ansClient = &piperhttp.Client{}
	err = ans.ansClient.SetBearerToken(serviceKey.OauthUrl,
		serviceKey.ClientId,
		serviceKey.ClientSecret)
	if err != nil {
		return err
	}

	header := make(http.Header)
	header.Add("Content-Type", "application/json")
	response, err := ans.ansClient.SendRequest(http.MethodPost,
		"https://clm-sl-ans-live-ans-service-api.cfapps.eu10.hana.ondemand.com/cf/producer/v1/resource-events",
		bytes.NewBuffer([]byte(body)), header, nil)
	if err != nil {
		return err
	}

	log.Entry().Infof("XXXXXXXXX Status code: %d", response.StatusCode)

	return nil
}

func (ans *ANS) SendNoToken() error {
	header := make(http.Header)
	header.Add("Content-Type", "application/json")
	response, err := ans.ansClient.SendRequest(http.MethodPost,
		"https://clm-sl-ans-live-ans-service-api.cfapps.eu10.hana.ondemand.com/cf/producer/v1/resource-events",
		bytes.NewBuffer([]byte(body)), header, nil)
	if err != nil {
		return err
	}

	log.Entry().Infof("XXXXXXXXX Status code: %d", response.StatusCode)

	return nil
}
