PACTICIPANT := "contract-testing"
PACT_CLI="docker run --rm -v ${PWD}:${PWD} -e PACT_BROKER_BASE_URL -e PACT_BROKER_TOKEN pactfoundation/pact-cli"

deploy:
	@echo "All tests passed - now we can safely deploy"

can_i_deploy:
	"${PACT_CLI}" broker can-i-deploy --pacticipant ${PACTICIPANT} --version "0.0.1"

