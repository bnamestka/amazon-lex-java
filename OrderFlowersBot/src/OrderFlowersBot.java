package com.screamingdata;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.lexmodelbuilding.AmazonLexModelBuilding;
import com.amazonaws.services.lexmodelbuilding.AmazonLexModelBuildingClientBuilder;
import com.amazonaws.services.lexmodelbuilding.model.*;

import java.util.ArrayList;
import java.util.List;

public class OrderFlowersBot {

    public static void main(String[] args) {
        // Create LexModelBuilding client
        AWSCredentials awsCreds = new BasicAWSCredentials("",//IAM user's ACCESS_KEY
                                                            "");//IAM user's SECRET_KEY

        AmazonLexModelBuilding modelBuildingClient = AmazonLexModelBuildingClientBuilder.standard()
                .withRegion(Regions.US_EAST_1)
                .withCredentials(new AWSStaticCredentialsProvider(awsCreds))
                .build();

        // Create Slot Type
        // Create PutSlotTypeRequest
        // (See:  http://docs.aws.amazon.com/AWSJavaSDK/latest/javadoc/index.html?com/amazonaws/services/lexmodelbuilding/model/PutBotRequest.html)

        PutSlotTypeRequest putSlotTypeRequest = new PutSlotTypeRequest().withName("FlowerTypes").withDescription("Types of flowers to pick up");
        // create EnumerationValue list
        EnumerationValue enumValue;
        List<EnumerationValue> enumerationValues = new ArrayList<>();
        enumValue = new EnumerationValue().withValue("tulips");
        enumerationValues.add(enumValue);
        enumValue = new EnumerationValue().withValue("lilies");
        enumerationValues.add(enumValue);
        enumValue = new EnumerationValue().withValue("roses");
        enumerationValues.add(enumValue);
        // add EnumerationValue list to PutSlotTypeRequest
        putSlotTypeRequest.setEnumerationValues(enumerationValues);

        // Call putSlotType with the above defined request to create the slot type
        // (See: http://docs.aws.amazon.com/AWSJavaSDK/latest/javadoc/com/amazonaws/services/lexmodelbuilding/AmazonLexModelBuilding.html#putSlotType-com.amazonaws.services.lexmodelbuilding.model.PutSlotTypeRequest-)

        PutSlotTypeResult putSlotTypeResult = modelBuildingClient.putSlotType(putSlotTypeRequest);
        System.out.printf("putSlotTypeResult contains checksum: %s%n", putSlotTypeResult.getChecksum());

        // ---------------------------------------------------------------------------------------------------------
        // Create an Intent
        // Note: includes specification of slots, sample utterances, confirmation prompts, dialog code hooks, et.al.

        // Create a new PutIntentRequest:
        PutIntentRequest putIntentRequest = new PutIntentRequest().withName("OrderFlowers");

        // configure confirmationPrompt
        Prompt confirmationPrompt = new Prompt();
        confirmationPrompt.setMaxAttempts(2);
        List<Message> confirmationPromptMessageList = new ArrayList<>();
        Message confirmationPromptMessage = new Message().withContent("Okay, your {FlowerType} will be ready for pickup by {PickupTime} on {PickupDate}.  Does this sound okay?").withContentType(ContentType.PlainText);
        confirmationPromptMessageList.add(confirmationPromptMessage);
        confirmationPrompt.setMessages(confirmationPromptMessageList);
        // end of confirmationPrompt configuration, add to putIntentRequest
        putIntentRequest.setConfirmationPrompt(confirmationPrompt);

        // configure rejectionStatement
        Statement rejectionStatement = new Statement();
        List<Message> rejectionStatementMessageList = new ArrayList<>();
        Message rejectionStatementMessage = new Message().withContent("Okay, I will not place your order.").withContentType(ContentType.PlainText);
        rejectionStatementMessageList.add(rejectionStatementMessage);
        rejectionStatement.setMessages(rejectionStatementMessageList);
        // end of rejectionStatement configuration, add to putIntentRequest
        putIntentRequest.setRejectionStatement(rejectionStatement);

        // configure sampleUtterances
        List<String> sampleUtterances = new ArrayList<>();
        sampleUtterances.add("I would like to pick up flowers");
        sampleUtterances.add("I would like to order some flowers");
        // end of sampleUtterances configuration, add to putIntentRequest
        putIntentRequest.setSampleUtterances(sampleUtterances);

        // create Slots list
        List<Slot> slots = new ArrayList<>();

        // create and configure FlowerType slot
        Slot flowerTypeSlot = new Slot().withSlotType("FlowerTypes").withName("FlowerType");

        // configure valueElicitationPrompt for this slot
        Prompt valueElicitationPrompt = new Prompt();
        valueElicitationPrompt.setMaxAttempts(2);

        List<Message> valueElicitationPromptMessageList = new ArrayList<>();
        Message valueElicitationPromptMessage = new Message().withContent("What type of flowers would you like to order?").withContentType(ContentType.PlainText);
        valueElicitationPromptMessageList.add(valueElicitationPromptMessage);
        valueElicitationPrompt.setMessages(valueElicitationPromptMessageList);
        // add valueElicitationPrompt to slot
        flowerTypeSlot.setValueElicitationPrompt(valueElicitationPrompt);

        flowerTypeSlot.setSlotConstraint(SlotConstraint.Required);
        flowerTypeSlot.setPriority(1);
        flowerTypeSlot.setSlotTypeVersion("$LATEST");
        flowerTypeSlot.setDescription("The type of flowers to pick up");

        // configure sampleUtterances for this slot
        List<String> slotSampleUtterances = new ArrayList<>();
        slotSampleUtterances.add("I would like to order {FlowerType}");
        // end of slotSampleUtterances configuration, add to slot
        flowerTypeSlot.setSampleUtterances(slotSampleUtterances);

        // add slot to slots list
        slots.add(flowerTypeSlot);

        //
        // Create and configure PickupDate slot
        Slot pickupDateSlot = new Slot().withSlotType("AMAZON.DATE").withName("PickupDate");

        // configure valueElicitationPrompt for this slot
        valueElicitationPrompt = new Prompt();
        valueElicitationPrompt.setMaxAttempts(2);
        valueElicitationPromptMessageList = new ArrayList<>();
        valueElicitationPromptMessage = new Message().withContent("What day do you want the {FlowerType} to be picked up?").withContentType(ContentType.PlainText);
        valueElicitationPromptMessageList.add(valueElicitationPromptMessage);
        valueElicitationPrompt.setMessages(valueElicitationPromptMessageList);
        // add valueElicitationPrompt to slot
        pickupDateSlot.setValueElicitationPrompt(valueElicitationPrompt);

        pickupDateSlot.setSlotConstraint(SlotConstraint.Required);
        pickupDateSlot.setPriority(2);
        pickupDateSlot.setDescription("The date to pick up the flowers");

        // add slot to slots list
        slots.add(pickupDateSlot);

        // Create and configure PickupTime slot
        Slot pickupTimeSlot = new Slot().withSlotType("AMAZON.TIME").withName("PickupTime");

        // configure valueElicitationPrompt for this slot
        valueElicitationPrompt = new Prompt();
        valueElicitationPrompt.setMaxAttempts(2);
        valueElicitationPromptMessageList = new ArrayList<>();
        valueElicitationPromptMessage = new Message().withContent("Pick up the {FlowerType} at what time on {PickupDate}?").withContentType(ContentType.PlainText);
        valueElicitationPromptMessageList.add(valueElicitationPromptMessage);
        valueElicitationPrompt.setMessages(valueElicitationPromptMessageList);
        // add valueElicitationPrompt to slot
        pickupTimeSlot.setValueElicitationPrompt(valueElicitationPrompt);

        pickupTimeSlot.setSlotConstraint(SlotConstraint.Required);
        pickupTimeSlot.setPriority(3);
        pickupTimeSlot.setDescription("The time to pick up the flowers");

        // add slot to slots list
        slots.add(pickupTimeSlot);

        // Now add slots list to the putIntentRequest
        putIntentRequest.setSlots(slots);

        // ----------  end of slot creation  --------------------------

        // configure fulfillmentActivity and add to putIntentRequest
        FulfillmentActivity fulfillmentActivity = new FulfillmentActivity().withType(FulfillmentActivityType.ReturnIntent);
        putIntentRequest.setFulfillmentActivity(fulfillmentActivity);

        // add description to putIntentRequest
        putIntentRequest.setDescription("Intent to order a bouquet of flowers for pick up");

        // Finally, issue the request to create the OrderFlowers intent.
        PutIntentResult putIntentResult = modelBuildingClient.putIntent(putIntentRequest);
        System.out.printf("putIntentResult contains checksum: %s%n", putIntentResult.getChecksum());

        // ---------------------------------------------------------------------------------------------------------
        // And now, create the Bot!  Begin with creating the request
        PutBotRequest putBotRequest = new PutBotRequest().withName("OrderFlowersBot");

        // configure list of intents used in the bot
        List<Intent> intents = new ArrayList<>();
        Intent intent = new Intent().withIntentName("OrderFlowers").withIntentVersion("$LATEST");
        // add intent to intent list
        intents.add(intent);

        // add intents to bot request
        putBotRequest.setIntents(intents);

        // set putBotRequest locale
        putBotRequest.setLocale("en-US");

        // configure abortStatement
        Statement abortStatement = new Statement();
        List<Message> abortStatementMessageList = new ArrayList<>();
        Message abortStatementMessage = new Message().withContent("Sorry, I'm not able to assist at this time").withContentType(ContentType.PlainText);
        abortStatementMessageList.add(abortStatementMessage);
        abortStatement.setMessages(abortStatementMessageList);
        // end of abortStatement configuration, add to putBotRequest
        putBotRequest.setAbortStatement(abortStatement);

        // configure clarificationPrompt
        Prompt clarificationPrompt = new Prompt();
        clarificationPrompt.setMaxAttempts(2);
        List<Message> clarificationPromptMessageList = new ArrayList<>();
        Message clarificationPromptMessage = new Message().withContent("I didn't understand you, what would you like to do?").withContentType(ContentType.PlainText);
        clarificationPromptMessageList.add(clarificationPromptMessage);
        clarificationPrompt.setMessages(clarificationPromptMessageList);

        // end of clarificationPrompt configuration, add to putBotRequest
        putBotRequest.setClarificationPrompt(clarificationPrompt);

        // add voice id, subject to COPPA, set idle TTL and description
        putBotRequest.setVoiceId("Salli");
        putBotRequest.setChildDirected(false);
        putBotRequest.setIdleSessionTTLInSeconds(600);
        putBotRequest.setDescription("Bot to order flowers on the behalf of a user");

        // PutBotRequest now complete.  Finally we can issue the request to create the OrderFlowersBot bot.
        PutBotResult putBotResult = modelBuildingClient.putBot(putBotRequest);
        System.out.printf("putBotResult contains checksum: %s%n", putBotResult.getChecksum());

    }
}
