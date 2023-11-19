############# AWS CONFIGURATION #############################
terraform {
  required_version = ">=1.6.2"

  required_providers {
    aws = {
      source  = "hashicorp/aws"
      version = "~> 5.26.0"
    }
  }
}

provider "aws" {
  region  = "eu-central-1"
  profile = "default"

  default_tags {
    tags = {
      Terraform = "true"
      Stack     = "demo-contract-testing"
    }
  }

  #  Usually you do want to create a backend to store the state remotely, but for this demo the local state will be enough.
}


####################### QUEUE #############################
resource "aws_sqs_queue" "contract_testing" {
  name                      = "contract-testing"
  delay_seconds             = 0
  receive_wait_time_seconds = 0
}
