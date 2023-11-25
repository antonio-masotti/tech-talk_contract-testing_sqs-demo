PACTICIPANT := "contract-testing"
PACT_CLI="docker run --rm -v ${PWD}:${PWD} pactfoundation/pact-cli:latest"

deploy:
	@echo "All tests passed - now we can safely deploy"

can_i_deploy:
	"${PACT_CLI}" broker can-i-deploy --pacticipant ${PACTICIPANT} --version "0.0.1" --broker-base-url ${PACT_BROKER_URL} --broker-token ${PACT_BROKER_TOKEN}

