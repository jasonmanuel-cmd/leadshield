package com.leadshield.app.data

data class MessageTemplate(
    val id: String,
    val name: String,
    val industry: String,
    val message: String
)

object MessageTemplates {

    val templates = listOf(
        MessageTemplate(
            id = "plumbing_1",
            name = "Plumbing Option 1",
            industry = "Plumbing",
            message = "Hi! I'm currently on a plumbing job and my hands are full. Please text me your address and a brief description of the issue."
        ),
        MessageTemplate(
            id = "plumbing_2",
            name = "Plumbing Option 2",
            industry = "Plumbing",
            message = "Thanks for calling! I am under a sink right now. Text me if it's an emergency leak and I'll get back to you ASAP."
        ),
        MessageTemplate(
            id = "plumbing_3",
            name = "Plumbing Option 3",
            industry = "Plumbing",
            message = "Hello, this is [Name]. I'm currently with a client. Please text me your plumbing issue and I'll return your message shortly."
        ),
        MessageTemplate(
            id = "plumbing_4",
            name = "Plumbing Option 4",
            industry = "Plumbing",
            message = "Hi there! I'm out in the field. Please leave a text with what you need repaired or installed and I'll call back."
        ),
        MessageTemplate(
            id = "plumbing_5",
            name = "Plumbing Option 5",
            industry = "Plumbing",
            message = "Sorry I missed your call! I'm currently driving between service calls. Please text your details."
        ),
        MessageTemplate(
            id = "plumbing_6",
            name = "Plumbing Option 6",
            industry = "Plumbing",
            message = "Hello! I am dealing with a water emergency right now. Please text your name and the issue you're facing."
        ),
        MessageTemplate(
            id = "plumbing_7",
            name = "Plumbing Option 7",
            industry = "Plumbing",
            message = "Thanks for reaching out! I'm currently busy with a pipe repair. I'll get back to you as soon as I finish."
        ),
        MessageTemplate(
            id = "plumbing_8",
            name = "Plumbing Option 8",
            industry = "Plumbing",
            message = "Hi! I'm currently unavailable. If you need a plumbing estimate, please text me your address and what you need done."
        ),
        MessageTemplate(
            id = "plumbing_9",
            name = "Plumbing Option 9",
            industry = "Plumbing",
            message = "Hello! I am currently on a service call. Please text 'PLUMBING' with your issue and I'll reply promptly."
        ),
        MessageTemplate(
            id = "plumbing_10",
            name = "Plumbing Option 10",
            industry = "Plumbing",
            message = "Thanks for calling! If this is a plumbing emergency, text 'URGENT' along with your address. Otherwise, I'll call you back soon."
        ),
        MessageTemplate(
            id = "electrical_1",
            name = "Electrical Option 1",
            industry = "Electrical",
            message = "Hi! I'm currently working on an electrical panel and can't answer. Please text me your issue."
        ),
        MessageTemplate(
            id = "electrical_2",
            name = "Electrical Option 2",
            industry = "Electrical",
            message = "Thanks for calling! I am dealing with high voltage right now. Please text your name and job details."
        ),
        MessageTemplate(
            id = "electrical_3",
            name = "Electrical Option 3",
            industry = "Electrical",
            message = "Hello! I am currently on a service call. Please text me if you need an estimate or emergency repair."
        ),
        MessageTemplate(
            id = "electrical_4",
            name = "Electrical Option 4",
            industry = "Electrical",
            message = "Hi there! My hands are tied up with wires right now. Please leave a text and I'll get right back to you."
        ),
        MessageTemplate(
            id = "electrical_5",
            name = "Electrical Option 5",
            industry = "Electrical",
            message = "Sorry I missed you! I am currently driving to my next electrical job. Please text your details."
        ),
        MessageTemplate(
            id = "electrical_6",
            name = "Electrical Option 6",
            industry = "Electrical",
            message = "Hello! If you have a power outage or emergency, please text 'URGENT' with your address."
        ),
        MessageTemplate(
            id = "electrical_7",
            name = "Electrical Option 7",
            industry = "Electrical",
            message = "Thanks for reaching out! I'm busy with an installation. I'll call you back as soon as I'm done."
        ),
        MessageTemplate(
            id = "electrical_8",
            name = "Electrical Option 8",
            industry = "Electrical",
            message = "Hi! I'm currently unavailable. For lighting or wiring estimates, please text me your requirements."
        ),
        MessageTemplate(
            id = "electrical_9",
            name = "Electrical Option 9",
            industry = "Electrical",
            message = "Hello! I am on a job site and can't hear my phone. Please text your electrical issue."
        ),
        MessageTemplate(
            id = "electrical_10",
            name = "Electrical Option 10",
            industry = "Electrical",
            message = "Thanks for calling! Please text me your name and address, and I will call you back shortly."
        ),
        MessageTemplate(
            id = "hvac_1",
            name = "HVAC Option 1",
            industry = "HVAC",
            message = "Hi! I'm currently servicing an AC/Heating unit. Please text me your issue and I'll call you back."
        ),
        MessageTemplate(
            id = "hvac_2",
            name = "HVAC Option 2",
            industry = "HVAC",
            message = "Thanks for calling! I'm up in an attic or on a roof right now. Please text your details."
        ),
        MessageTemplate(
            id = "hvac_3",
            name = "HVAC Option 3",
            industry = "HVAC",
            message = "Hello! I am currently on an HVAC service call. Please text if you need a repair or maintenance."
        ),
        MessageTemplate(
            id = "hvac_4",
            name = "HVAC Option 4",
            industry = "HVAC",
            message = "Hi there! I'm out in the field working on a system. Please leave a text and I'll get back to you."
        ),
        MessageTemplate(
            id = "hvac_5",
            name = "HVAC Option 5",
            industry = "HVAC",
            message = "Sorry I missed your call! I'm driving between HVAC jobs. Please text your address and the problem."
        ),
        MessageTemplate(
            id = "hvac_6",
            name = "HVAC Option 6",
            industry = "HVAC",
            message = "Hello! If your AC or heating is completely out, please text 'NO AC/HEAT' and your address."
        ),
        MessageTemplate(
            id = "hvac_7",
            name = "HVAC Option 7",
            industry = "HVAC",
            message = "Thanks for reaching out! I'm busy with an installation. I'll get back to you as soon as it's running."
        ),
        MessageTemplate(
            id = "hvac_8",
            name = "HVAC Option 8",
            industry = "HVAC",
            message = "Hi! I'm currently unavailable. If you need an estimate for a new system, please text me."
        ),
        MessageTemplate(
            id = "hvac_9",
            name = "HVAC Option 9",
            industry = "HVAC",
            message = "Hello! I am on a noisy job site right now. Please text your HVAC issue."
        ),
        MessageTemplate(
            id = "hvac_10",
            name = "HVAC Option 10",
            industry = "HVAC",
            message = "Thanks for calling! Please text me your name, address, and the issue with your unit."
        ),
        MessageTemplate(
            id = "roofing_1",
            name = "Roofing Option 1",
            industry = "Roofing",
            message = "Hi! I'm currently up on a roof and can't safely take your call. Please text me your details."
        ),
        MessageTemplate(
            id = "roofing_2",
            name = "Roofing Option 2",
            industry = "Roofing",
            message = "Thanks for calling! I'm inspecting a roof right now. Please text your address and the issue."
        ),
        MessageTemplate(
            id = "roofing_3",
            name = "Roofing Option 3",
            industry = "Roofing",
            message = "Hello! I am currently on a roofing job. Please text if you need a repair or full replacement estimate."
        ),
        MessageTemplate(
            id = "roofing_4",
            name = "Roofing Option 4",
            industry = "Roofing",
            message = "Hi there! I'm out in the field. Please leave a text and I'll call back when I'm on the ground."
        ),
        MessageTemplate(
            id = "roofing_5",
            name = "Roofing Option 5",
            industry = "Roofing",
            message = "Sorry I missed your call! I'm driving to my next inspection. Please text your contact details."
        ),
        MessageTemplate(
            id = "roofing_6",
            name = "Roofing Option 6",
            industry = "Roofing",
            message = "Hello! If you have an active roof leak, please text 'LEAK' along with your address."
        ),
        MessageTemplate(
            id = "roofing_7",
            name = "Roofing Option 7",
            industry = "Roofing",
            message = "Thanks for reaching out! I'm busy with a roof installation. I'll get back to you soon."
        ),
        MessageTemplate(
            id = "roofing_8",
            name = "Roofing Option 8",
            industry = "Roofing",
            message = "Hi! I'm currently unavailable. If you need an estimate, please text me your address."
        ),
        MessageTemplate(
            id = "roofing_9",
            name = "Roofing Option 9",
            industry = "Roofing",
            message = "Hello! I am on a noisy job site right now. Please text your roofing issue."
        ),
        MessageTemplate(
            id = "roofing_10",
            name = "Roofing Option 10",
            industry = "Roofing",
            message = "Thanks for calling! Please text me your name, address, and what roofing service you need."
        ),
        MessageTemplate(
            id = "construction_1",
            name = "Construction Option 1",
            industry = "Construction",
            message = "Hi! Working with power tools and can't hear the phone! Please text me your project details."
        ),
        MessageTemplate(
            id = "construction_2",
            name = "Construction Option 2",
            industry = "Construction",
            message = "Thanks for calling! I'm currently on a construction site. Please text your name and what you need."
        ),
        MessageTemplate(
            id = "construction_3",
            name = "Construction Option 3",
            industry = "Construction",
            message = "Hello! I am currently busy with a build. Please text if you need an estimate or have questions."
        ),
        MessageTemplate(
            id = "construction_4",
            name = "Construction Option 4",
            industry = "Construction",
            message = "Hi there! I'm out in the field framing or finishing. Please leave a text and I'll get back to you."
        ),
        MessageTemplate(
            id = "construction_5",
            name = "Construction Option 5",
            industry = "Construction",
            message = "Sorry I missed your call! I'm driving between sites. Please text your contact details."
        ),
        MessageTemplate(
            id = "construction_6",
            name = "Construction Option 6",
            industry = "Construction",
            message = "Hello! If this is regarding an ongoing project, please text me the details."
        ),
        MessageTemplate(
            id = "construction_7",
            name = "Construction Option 7",
            industry = "Construction",
            message = "Thanks for reaching out! I'm busy with a client. I'll get back to you later today."
        ),
        MessageTemplate(
            id = "construction_8",
            name = "Construction Option 8",
            industry = "Construction",
            message = "Hi! I'm currently unavailable. If you need a remodel or construction estimate, please text me."
        ),
        MessageTemplate(
            id = "construction_9",
            name = "Construction Option 9",
            industry = "Construction",
            message = "Hello! The job site is too loud for a call right now. Please text your message."
        ),
        MessageTemplate(
            id = "construction_10",
            name = "Construction Option 10",
            industry = "Construction",
            message = "Thanks for calling! Please text me your name, address, and what you're looking to build."
        ),
        MessageTemplate(
            id = "landscaping_1",
            name = "Landscaping Option 1",
            industry = "Landscaping",
            message = "Hi! I'm currently operating loud lawn equipment. Please text me your landscaping needs."
        ),
        MessageTemplate(
            id = "landscaping_2",
            name = "Landscaping Option 2",
            industry = "Landscaping",
            message = "Thanks for calling! I'm out in the field planting and pruning. Please text your address."
        ),
        MessageTemplate(
            id = "landscaping_3",
            name = "Landscaping Option 3",
            industry = "Landscaping",
            message = "Hello! I am currently on a landscaping job. Please text if you need maintenance or a new design."
        ),
        MessageTemplate(
            id = "landscaping_4",
            name = "Landscaping Option 4",
            industry = "Landscaping",
            message = "Hi there! My hands are covered in dirt right now! Please leave a text and I'll get back to you."
        ),
        MessageTemplate(
            id = "landscaping_5",
            name = "Landscaping Option 5",
            industry = "Landscaping",
            message = "Sorry I missed your call! I'm driving between properties. Please text your contact details."
        ),
        MessageTemplate(
            id = "landscaping_6",
            name = "Landscaping Option 6",
            industry = "Landscaping",
            message = "Hello! If you need urgent yard cleanup, please text 'CLEANUP' along with your address."
        ),
        MessageTemplate(
            id = "landscaping_7",
            name = "Landscaping Option 7",
            industry = "Landscaping",
            message = "Thanks for reaching out! I'm busy with an installation. I'll call you back when I'm finished."
        ),
        MessageTemplate(
            id = "landscaping_8",
            name = "Landscaping Option 8",
            industry = "Landscaping",
            message = "Hi! I'm currently unavailable. If you need an estimate for yard work, please text me."
        ),
        MessageTemplate(
            id = "landscaping_9",
            name = "Landscaping Option 9",
            industry = "Landscaping",
            message = "Hello! The mowers are running so I can't hear the phone. Please text your message."
        ),
        MessageTemplate(
            id = "landscaping_10",
            name = "Landscaping Option 10",
            industry = "Landscaping",
            message = "Thanks for calling! Please text me your name, address, and what landscaping services you need."
        ),
        MessageTemplate(
            id = "painting_1",
            name = "Painting Option 1",
            industry = "Painting",
            message = "Hi! In the middle of a coat of paint! I'll call you back as soon as I can put the brush down."
        ),
        MessageTemplate(
            id = "painting_2",
            name = "Painting Option 2",
            industry = "Painting",
            message = "Thanks for calling! I'm up on a ladder painting right now. Please text your address and project details."
        ),
        MessageTemplate(
            id = "painting_3",
            name = "Painting Option 3",
            industry = "Painting",
            message = "Hello! I am currently on a painting job. Please text if you need an interior or exterior estimate."
        ),
        MessageTemplate(
            id = "painting_4",
            name = "Painting Option 4",
            industry = "Painting",
            message = "Hi there! I'm prepping a house for painting. Please leave a text and I'll get back to you."
        ),
        MessageTemplate(
            id = "painting_5",
            name = "Painting Option 5",
            industry = "Painting",
            message = "Sorry I missed your call! I'm driving between paint jobs. Please text your contact details."
        ),
        MessageTemplate(
            id = "painting_6",
            name = "Painting Option 6",
            industry = "Painting",
            message = "Hello! If this is regarding scheduling a paint job, please text me the details."
        ),
        MessageTemplate(
            id = "painting_7",
            name = "Painting Option 7",
            industry = "Painting",
            message = "Thanks for reaching out! I'm busy finishing a room. I'll get back to you later today."
        ),
        MessageTemplate(
            id = "painting_8",
            name = "Painting Option 8",
            industry = "Painting",
            message = "Hi! I'm currently unavailable. If you need a painting estimate, please text me."
        ),
        MessageTemplate(
            id = "painting_9",
            name = "Painting Option 9",
            industry = "Painting",
            message = "Hello! I am currently spraying and can't take a call. Please text your message."
        ),
        MessageTemplate(
            id = "painting_10",
            name = "Painting Option 10",
            industry = "Painting",
            message = "Thanks for calling! Please text me your name, address, and what you need painted."
        ),
        MessageTemplate(
            id = "cleaning_1",
            name = "Cleaning Option 1",
            industry = "Cleaning",
            message = "Hi! Currently at a client's home cleaning. Please text me your name and when you're looking for a clean."
        ),
        MessageTemplate(
            id = "cleaning_2",
            name = "Cleaning Option 2",
            industry = "Cleaning",
            message = "Thanks for calling! My hands are full of cleaning supplies right now. Please text your details."
        ),
        MessageTemplate(
            id = "cleaning_3",
            name = "Cleaning Option 3",
            industry = "Cleaning",
            message = "Hello! I am currently on a cleaning job. Please text if you need a deep clean or regular service."
        ),
        MessageTemplate(
            id = "cleaning_4",
            name = "Cleaning Option 4",
            industry = "Cleaning",
            message = "Hi there! I'm vacuuming and can't hear the phone. Please leave a text and I'll get back to you."
        ),
        MessageTemplate(
            id = "cleaning_5",
            name = "Cleaning Option 5",
            industry = "Cleaning",
            message = "Sorry I missed your call! I'm driving between houses. Please text your contact details."
        ),
        MessageTemplate(
            id = "cleaning_6",
            name = "Cleaning Option 6",
            industry = "Cleaning",
            message = "Hello! If you need an urgent move-out clean, please text 'URGENT CLEAN' along with your address."
        ),
        MessageTemplate(
            id = "cleaning_7",
            name = "Cleaning Option 7",
            industry = "Cleaning",
            message = "Thanks for reaching out! I'm busy detailing a home. I'll call you back when I'm finished."
        ),
        MessageTemplate(
            id = "cleaning_8",
            name = "Cleaning Option 8",
            industry = "Cleaning",
            message = "Hi! I'm currently unavailable. If you need a cleaning estimate, please text me."
        ),
        MessageTemplate(
            id = "cleaning_9",
            name = "Cleaning Option 9",
            industry = "Cleaning",
            message = "Hello! I am currently organizing and cleaning. Please text your message."
        ),
        MessageTemplate(
            id = "cleaning_10",
            name = "Cleaning Option 10",
            industry = "Cleaning",
            message = "Thanks for calling! Please text me your name, address, and what cleaning services you need."
        ),
        MessageTemplate(
            id = "pest_control_1",
            name = "Pest Control Option 1",
            industry = "Pest Control",
            message = "Hi! In the middle of a treatment. Please text me your name and what kind of pests you're seeing."
        ),
        MessageTemplate(
            id = "pest_control_2",
            name = "Pest Control Option 2",
            industry = "Pest Control",
            message = "Thanks for calling! I'm currently spraying a property. Please text your address and the issue."
        ),
        MessageTemplate(
            id = "pest_control_3",
            name = "Pest Control Option 3",
            industry = "Pest Control",
            message = "Hello! I am currently on a pest control job. Please text if you need an inspection or treatment."
        ),
        MessageTemplate(
            id = "pest_control_4",
            name = "Pest Control Option 4",
            industry = "Pest Control",
            message = "Hi there! I'm out in the field eliminating pests. Please leave a text and I'll get back to you."
        ),
        MessageTemplate(
            id = "pest_control_5",
            name = "Pest Control Option 5",
            industry = "Pest Control",
            message = "Sorry I missed your call! I'm driving between appointments. Please text your contact details."
        ),
        MessageTemplate(
            id = "pest_control_6",
            name = "Pest Control Option 6",
            industry = "Pest Control",
            message = "Hello! If you have an urgent pest issue like wasps or rodents, please text 'URGENT' and your address."
        ),
        MessageTemplate(
            id = "pest_control_7",
            name = "Pest Control Option 7",
            industry = "Pest Control",
            message = "Thanks for reaching out! I'm busy inspecting a home. I'll call you back soon."
        ),
        MessageTemplate(
            id = "pest_control_8",
            name = "Pest Control Option 8",
            industry = "Pest Control",
            message = "Hi! I'm currently unavailable. If you need a pest control estimate, please text me."
        ),
        MessageTemplate(
            id = "pest_control_9",
            name = "Pest Control Option 9",
            industry = "Pest Control",
            message = "Hello! I am currently wearing protective gear and can't take a call. Please text your message."
        ),
        MessageTemplate(
            id = "pest_control_10",
            name = "Pest Control Option 10",
            industry = "Pest Control",
            message = "Thanks for calling! Please text me your name, address, and what pests you need handled."
        ),
        MessageTemplate(
            id = "handyman_1",
            name = "Handyman Option 1",
            industry = "Handyman",
            message = "Hi! I'm currently on a service call. Please text me a list of what you need fixed."
        ),
        MessageTemplate(
            id = "handyman_2",
            name = "Handyman Option 2",
            industry = "Handyman",
            message = "Thanks for calling! I'm working on a repair right now. Please text your address and the issue."
        ),
        MessageTemplate(
            id = "handyman_3",
            name = "Handyman Option 3",
            industry = "Handyman",
            message = "Hello! I am currently busy with a handyman job. Please text if you need a repair or installation."
        ),
        MessageTemplate(
            id = "handyman_4",
            name = "Handyman Option 4",
            industry = "Handyman",
            message = "Hi there! I'm out in the field fixing things. Please leave a text and I'll get back to you."
        ),
        MessageTemplate(
            id = "handyman_5",
            name = "Handyman Option 5",
            industry = "Handyman",
            message = "Sorry I missed your call! I'm driving to my next job. Please text your contact details."
        ),
        MessageTemplate(
            id = "handyman_6",
            name = "Handyman Option 6",
            industry = "Handyman",
            message = "Hello! If you have an urgent repair need, please text 'URGENT' along with your address."
        ),
        MessageTemplate(
            id = "handyman_7",
            name = "Handyman Option 7",
            industry = "Handyman",
            message = "Thanks for reaching out! I'm busy finishing a project. I'll call you back later."
        ),
        MessageTemplate(
            id = "handyman_8",
            name = "Handyman Option 8",
            industry = "Handyman",
            message = "Hi! I'm currently unavailable. If you need an estimate for some repairs, please text me."
        ),
        MessageTemplate(
            id = "handyman_9",
            name = "Handyman Option 9",
            industry = "Handyman",
            message = "Hello! I am on a noisy job site right now. Please text your message."
        ),
        MessageTemplate(
            id = "handyman_10",
            name = "Handyman Option 10",
            industry = "Handyman",
            message = "Thanks for calling! Please text me your name, address, and what handyman services you need."
        ),
        MessageTemplate(
            id = "auto_repair_1",
            name = "Auto Repair Option 1",
            industry = "Auto Repair",
            message = "Hi! I'm currently under a car right now! Please text me your name and the make/model of your vehicle."
        ),
        MessageTemplate(
            id = "auto_repair_2",
            name = "Auto Repair Option 2",
            industry = "Auto Repair",
            message = "Thanks for calling! I'm covered in grease at the moment. Please text your details."
        ),
        MessageTemplate(
            id = "auto_repair_3",
            name = "Auto Repair Option 3",
            industry = "Auto Repair",
            message = "Hello! I am currently diagnosing a vehicle. Please text if you need an appointment or estimate."
        ),
        MessageTemplate(
            id = "auto_repair_4",
            name = "Auto Repair Option 4",
            industry = "Auto Repair",
            message = "Hi there! I'm in the shop working on a repair. Please leave a text and I'll get back to you."
        ),
        MessageTemplate(
            id = "auto_repair_5",
            name = "Auto Repair Option 5",
            industry = "Auto Repair",
            message = "Sorry I missed your call! I'm test driving a vehicle. Please text your contact details."
        ),
        MessageTemplate(
            id = "auto_repair_6",
            name = "Auto Repair Option 6",
            industry = "Auto Repair",
            message = "Hello! If your car is broken down and you need a tow to the shop, please text 'TOW' and your location."
        ),
        MessageTemplate(
            id = "auto_repair_7",
            name = "Auto Repair Option 7",
            industry = "Auto Repair",
            message = "Thanks for reaching out! I'm busy with an engine repair. I'll call you back soon."
        ),
        MessageTemplate(
            id = "auto_repair_8",
            name = "Auto Repair Option 8",
            industry = "Auto Repair",
            message = "Hi! I'm currently unavailable. If you need a quote for an auto repair, please text me."
        ),
        MessageTemplate(
            id = "auto_repair_9",
            name = "Auto Repair Option 9",
            industry = "Auto Repair",
            message = "Hello! The shop is too loud to hear the phone right now. Please text your message."
        ),
        MessageTemplate(
            id = "auto_repair_10",
            name = "Auto Repair Option 10",
            industry = "Auto Repair",
            message = "Thanks for calling! Please text me your name, vehicle info, and what repair you need."
        ),
        MessageTemplate(
            id = "towing_1",
            name = "Towing Option 1",
            industry = "Towing",
            message = "Hi! Currently on a recovery. Please text your exact location and the vehicle type."
        ),
        MessageTemplate(
            id = "towing_2",
            name = "Towing Option 2",
            industry = "Towing",
            message = "Thanks for calling! I'm hooking up a vehicle right now. Please text your location and issue."
        ),
        MessageTemplate(
            id = "towing_3",
            name = "Towing Option 3",
            industry = "Towing",
            message = "Hello! I am currently towing a car. Please text if you need a tow, jumpstart, or lockout service."
        ),
        MessageTemplate(
            id = "towing_4",
            name = "Towing Option 4",
            industry = "Towing",
            message = "Hi there! I'm out on the road. Please leave a text with your location and I'll give you an ETA."
        ),
        MessageTemplate(
            id = "towing_5",
            name = "Towing Option 5",
            industry = "Towing",
            message = "Sorry I missed your call! I'm driving to a call. Please text your exact location."
        ),
        MessageTemplate(
            id = "towing_6",
            name = "Towing Option 6",
            industry = "Towing",
            message = "Hello! If this is an emergency tow from a highway, please text 'EMERGENCY' and your location."
        ),
        MessageTemplate(
            id = "towing_7",
            name = "Towing Option 7",
            industry = "Towing",
            message = "Thanks for reaching out! I'm busy loading a vehicle. I'll call you back immediately after."
        ),
        MessageTemplate(
            id = "towing_8",
            name = "Towing Option 8",
            industry = "Towing",
            message = "Hi! I'm currently unavailable. If you need a quote for a long-distance tow, please text me."
        ),
        MessageTemplate(
            id = "towing_9",
            name = "Towing Option 9",
            industry = "Towing",
            message = "Hello! I am currently securing a load and can't take a call. Please text your message."
        ),
        MessageTemplate(
            id = "towing_10",
            name = "Towing Option 10",
            industry = "Towing",
            message = "Thanks for calling! Please text me your name, location, and what towing service you need."
        ),
        MessageTemplate(
            id = "locksmith_1",
            name = "Locksmith Option 1",
            industry = "Locksmith",
            message = "Hi! On a lockout call. Please text 'LOCKED OUT' with your location and I'll call you back."
        ),
        MessageTemplate(
            id = "locksmith_2",
            name = "Locksmith Option 2",
            industry = "Locksmith",
            message = "Thanks for calling! I'm currently picking a lock. Please text your address and the issue."
        ),
        MessageTemplate(
            id = "locksmith_3",
            name = "Locksmith Option 3",
            industry = "Locksmith",
            message = "Hello! I am currently on a locksmith job. Please text if you need an auto, home, or business unlock."
        ),
        MessageTemplate(
            id = "locksmith_4",
            name = "Locksmith Option 4",
            industry = "Locksmith",
            message = "Hi there! I'm out in the field making keys. Please leave a text and I'll get back to you."
        ),
        MessageTemplate(
            id = "locksmith_5",
            name = "Locksmith Option 5",
            industry = "Locksmith",
            message = "Sorry I missed your call! I'm driving to my next unlock. Please text your contact details."
        ),
        MessageTemplate(
            id = "locksmith_6",
            name = "Locksmith Option 6",
            industry = "Locksmith",
            message = "Hello! If this is an emergency lockout, please text 'URGENT' along with your exact location."
        ),
        MessageTemplate(
            id = "locksmith_7",
            name = "Locksmith Option 7",
            industry = "Locksmith",
            message = "Thanks for reaching out! I'm busy rekeying a house. I'll call you back when I'm finished."
        ),
        MessageTemplate(
            id = "locksmith_8",
            name = "Locksmith Option 8",
            industry = "Locksmith",
            message = "Hi! I'm currently unavailable. If you need an estimate for new locks, please text me."
        ),
        MessageTemplate(
            id = "locksmith_9",
            name = "Locksmith Option 9",
            industry = "Locksmith",
            message = "Hello! I am currently cutting a key and the machine is loud. Please text your message."
        ),
        MessageTemplate(
            id = "locksmith_10",
            name = "Locksmith Option 10",
            industry = "Locksmith",
            message = "Thanks for calling! Please text me your name, location, and what locksmith service you need."
        ),
        MessageTemplate(
            id = "real_estate_1",
            name = "Real Estate Option 1",
            industry = "Real Estate",
            message = "Hi! Currently showing a property to a client. Please text me which property you're interested in."
        ),
        MessageTemplate(
            id = "real_estate_2",
            name = "Real Estate Option 2",
            industry = "Real Estate",
            message = "Thanks for calling! I'm at an open house right now. Please text your name and what you're looking for."
        ),
        MessageTemplate(
            id = "real_estate_3",
            name = "Real Estate Option 3",
            industry = "Real Estate",
            message = "Hello! I am currently in a closing meeting. Please text if you want to buy or sell a home."
        ),
        MessageTemplate(
            id = "real_estate_4",
            name = "Real Estate Option 4",
            industry = "Real Estate",
            message = "Hi there! I'm out showing homes. Please leave a text and I'll get back to you shortly."
        ),
        MessageTemplate(
            id = "real_estate_5",
            name = "Real Estate Option 5",
            industry = "Real Estate",
            message = "Sorry I missed your call! I'm driving between properties. Please text your contact details."
        ),
        MessageTemplate(
            id = "real_estate_6",
            name = "Real Estate Option 6",
            industry = "Real Estate",
            message = "Hello! If you want to schedule a viewing for a specific listing, please text me the address."
        ),
        MessageTemplate(
            id = "real_estate_7",
            name = "Real Estate Option 7",
            industry = "Real Estate",
            message = "Thanks for reaching out! I'm busy with a client consultation. I'll call you back later today."
        ),
        MessageTemplate(
            id = "real_estate_8",
            name = "Real Estate Option 8",
            industry = "Real Estate",
            message = "Hi! I'm currently unavailable. If you want a free home valuation, please text me your address."
        ),
        MessageTemplate(
            id = "real_estate_9",
            name = "Real Estate Option 9",
            industry = "Real Estate",
            message = "Hello! I am currently negotiating an offer and can't take a call. Please text your message."
        ),
        MessageTemplate(
            id = "real_estate_10",
            name = "Real Estate Option 10",
            industry = "Real Estate",
            message = "Thanks for calling! Please text me your name and whether you are looking to buy or sell."
        ),
        MessageTemplate(
            id = "fitness_1",
            name = "Fitness Option 1",
            industry = "Fitness",
            message = "Hi! In the middle of a training session! I'll call you back as soon as I'm finished."
        ),
        MessageTemplate(
            id = "fitness_2",
            name = "Fitness Option 2",
            industry = "Fitness",
            message = "Thanks for calling! I'm at the gym training a client right now. Please text your details."
        ),
        MessageTemplate(
            id = "fitness_3",
            name = "Fitness Option 3",
            industry = "Fitness",
            message = "Hello! I am currently coaching a class. Please text if you need to schedule a session."
        ),
        MessageTemplate(
            id = "fitness_4",
            name = "Fitness Option 4",
            industry = "Fitness",
            message = "Hi there! I'm in the middle of a workout. Please leave a text and I'll get back to you."
        ),
        MessageTemplate(
            id = "fitness_5",
            name = "Fitness Option 5",
            industry = "Fitness",
            message = "Sorry I missed your call! I'm between training sessions. Please text your contact details."
        ),
        MessageTemplate(
            id = "fitness_6",
            name = "Fitness Option 6",
            industry = "Fitness",
            message = "Hello! If you want to sign up for personal training, please text 'TRAIN' and your goals."
        ),
        MessageTemplate(
            id = "fitness_7",
            name = "Fitness Option 7",
            industry = "Fitness",
            message = "Thanks for reaching out! I'm busy with a fitness assessment. I'll call you back soon."
        ),
        MessageTemplate(
            id = "fitness_8",
            name = "Fitness Option 8",
            industry = "Fitness",
            message = "Hi! I'm currently unavailable. If you need info on my training packages, please text me."
        ),
        MessageTemplate(
            id = "fitness_9",
            name = "Fitness Option 9",
            industry = "Fitness",
            message = "Hello! The gym music is too loud to hear the phone right now. Please text your message."
        ),
        MessageTemplate(
            id = "fitness_10",
            name = "Fitness Option 10",
            industry = "Fitness",
            message = "Thanks for calling! Please text me your name and what your fitness goals are."
        ),
        MessageTemplate(
            id = "photography_1",
            name = "Photography Option 1",
            industry = "Photography",
            message = "Hi! Currently on a shoot. Please text me your name and the date you're interested in booking."
        ),
        MessageTemplate(
            id = "photography_2",
            name = "Photography Option 2",
            industry = "Photography",
            message = "Thanks for calling! I'm behind the camera right now. Please text your event details."
        ),
        MessageTemplate(
            id = "photography_3",
            name = "Photography Option 3",
            industry = "Photography",
            message = "Hello! I am currently editing photos. Please text if you need to book a session."
        ),
        MessageTemplate(
            id = "photography_4",
            name = "Photography Option 4",
            industry = "Photography",
            message = "Hi there! I'm out in the field shooting. Please leave a text and I'll get back to you."
        ),
        MessageTemplate(
            id = "photography_5",
            name = "Photography Option 5",
            industry = "Photography",
            message = "Sorry I missed your call! I'm driving to a location shoot. Please text your contact details."
        ),
        MessageTemplate(
            id = "photography_6",
            name = "Photography Option 6",
            industry = "Photography",
            message = "Hello! If you are inquiring about wedding photography, please text 'WEDDING' and your date."
        ),
        MessageTemplate(
            id = "photography_7",
            name = "Photography Option 7",
            industry = "Photography",
            message = "Thanks for reaching out! I'm busy with a portrait session. I'll call you back later today."
        ),
        MessageTemplate(
            id = "photography_8",
            name = "Photography Option 8",
            industry = "Photography",
            message = "Hi! I'm currently unavailable. If you need a quote for a photoshoot, please text me."
        ),
        MessageTemplate(
            id = "photography_9",
            name = "Photography Option 9",
            industry = "Photography",
            message = "Hello! I am currently in the studio and can't take a call. Please text your message."
        ),
        MessageTemplate(
            id = "photography_10",
            name = "Photography Option 10",
            industry = "Photography",
            message = "Thanks for calling! Please text me your name, the type of shoot you need, and the date."
        ),
        MessageTemplate(
            id = "delivery_1",
            name = "Delivery Option 1",
            industry = "Delivery",
            message = "Hi! On the road making deliveries. Please text me your name and any delivery instructions."
        ),
        MessageTemplate(
            id = "delivery_2",
            name = "Delivery Option 2",
            industry = "Delivery",
            message = "Thanks for calling! I'm carrying a package right now. Please text your address and the issue."
        ),
        MessageTemplate(
            id = "delivery_3",
            name = "Delivery Option 3",
            industry = "Delivery",
            message = "Hello! I am currently out for delivery. Please text if you need to check on a package."
        ),
        MessageTemplate(
            id = "delivery_4",
            name = "Delivery Option 4",
            industry = "Delivery",
            message = "Hi there! I'm driving my route. Please leave a text and I'll get back to you at my next stop."
        ),
        MessageTemplate(
            id = "delivery_5",
            name = "Delivery Option 5",
            industry = "Delivery",
            message = "Sorry I missed your call! I'm dropping off a delivery. Please text your contact details."
        ),
        MessageTemplate(
            id = "delivery_6",
            name = "Delivery Option 6",
            industry = "Delivery",
            message = "Hello! If you need to change a delivery address, please text the tracking number and new address."
        ),
        MessageTemplate(
            id = "delivery_7",
            name = "Delivery Option 7",
            industry = "Delivery",
            message = "Thanks for reaching out! I'm busy loading my truck. I'll call you back before I head out."
        ),
        MessageTemplate(
            id = "delivery_8",
            name = "Delivery Option 8",
            industry = "Delivery",
            message = "Hi! I'm currently unavailable. If you need a quote for courier services, please text me."
        ),
        MessageTemplate(
            id = "delivery_9",
            name = "Delivery Option 9",
            industry = "Delivery",
            message = "Hello! I am currently navigating traffic and can't take a call. Please text your message."
        ),
        MessageTemplate(
            id = "delivery_10",
            name = "Delivery Option 10",
            industry = "Delivery",
            message = "Thanks for calling! Please text me your name, tracking number, or delivery details."
        ),
        MessageTemplate(
            id = "beauty_salon_1",
            name = "Beauty Salon Option 1",
            industry = "Beauty Salon",
            message = "Hi! Currently with a client in the chair. Please text me your name and what time you're looking for."
        ),
        MessageTemplate(
            id = "beauty_salon_2",
            name = "Beauty Salon Option 2",
            industry = "Beauty Salon",
            message = "Thanks for calling! I'm in the middle of a color service right now. Please text your details."
        ),
        MessageTemplate(
            id = "beauty_salon_3",
            name = "Beauty Salon Option 3",
            industry = "Beauty Salon",
            message = "Hello! I am currently doing hair/nails. Please text if you need to book an appointment."
        ),
        MessageTemplate(
            id = "beauty_salon_4",
            name = "Beauty Salon Option 4",
            industry = "Beauty Salon",
            message = "Hi there! My hands are busy making someone beautiful! Please leave a text and I'll reply soon."
        ),
        MessageTemplate(
            id = "beauty_salon_5",
            name = "Beauty Salon Option 5",
            industry = "Beauty Salon",
            message = "Sorry I missed your call! I'm washing hair right now. Please text your contact details."
        ),
        MessageTemplate(
            id = "beauty_salon_6",
            name = "Beauty Salon Option 6",
            industry = "Beauty Salon",
            message = "Hello! If you need to cancel or reschedule, please text me at least 24 hours in advance."
        ),
        MessageTemplate(
            id = "beauty_salon_7",
            name = "Beauty Salon Option 7",
            industry = "Beauty Salon",
            message = "Thanks for reaching out! I'm busy with a bridal party. I'll call you back later today."
        ),
        MessageTemplate(
            id = "beauty_salon_8",
            name = "Beauty Salon Option 8",
            industry = "Beauty Salon",
            message = "Hi! I'm currently unavailable. If you need pricing for salon services, please text me."
        ),
        MessageTemplate(
            id = "beauty_salon_9",
            name = "Beauty Salon Option 9",
            industry = "Beauty Salon",
            message = "Hello! The blow dryers are running so I can't hear the phone. Please text your message."
        ),
        MessageTemplate(
            id = "beauty_salon_10",
            name = "Beauty Salon Option 10",
            industry = "Beauty Salon",
            message = "Thanks for calling! Please text me your name and what beauty service you would like to book."
        ),
        MessageTemplate(
            id = "catering_1",
            name = "Catering Option 1",
            industry = "Catering",
            message = "Hi! In the kitchen! Please text me your name and the date of your event."
        ),
        MessageTemplate(
            id = "catering_2",
            name = "Catering Option 2",
            industry = "Catering",
            message = "Thanks for calling! I'm currently prepping food for an event. Please text your details."
        ),
        MessageTemplate(
            id = "catering_3",
            name = "Catering Option 3",
            industry = "Catering",
            message = "Hello! I am currently at a catering gig. Please text if you need a quote for an upcoming event."
        ),
        MessageTemplate(
            id = "catering_4",
            name = "Catering Option 4",
            industry = "Catering",
            message = "Hi there! I'm cooking up a storm right now! Please leave a text and I'll get back to you."
        ),
        MessageTemplate(
            id = "catering_5",
            name = "Catering Option 5",
            industry = "Catering",
            message = "Sorry I missed your call! I'm driving the catering van to an event. Please text your contact details."
        ),
        MessageTemplate(
            id = "catering_6",
            name = "Catering Option 6",
            industry = "Catering",
            message = "Hello! If you are checking on an order for today, please text 'ORDER' and your name."
        ),
        MessageTemplate(
            id = "catering_7",
            name = "Catering Option 7",
            industry = "Catering",
            message = "Thanks for reaching out! I'm busy serving guests. I'll call you back tomorrow morning."
        ),
        MessageTemplate(
            id = "catering_8",
            name = "Catering Option 8",
            industry = "Catering",
            message = "Hi! I'm currently unavailable. If you need a menu and pricing, please text me your email address."
        ),
        MessageTemplate(
            id = "catering_9",
            name = "Catering Option 9",
            industry = "Catering",
            message = "Hello! It's loud in the kitchen right now. Please text your message."
        ),
        MessageTemplate(
            id = "catering_10",
            name = "Catering Option 10",
            industry = "Catering",
            message = "Thanks for calling! Please text me your name, event date, and guest count for catering."
        ),
        MessageTemplate(
            id = "consulting_1",
            name = "Consulting Option 1",
            industry = "Consulting",
            message = "Hi! Currently in a meeting with a client. Please text me a brief summary of what you'd like to discuss."
        ),
        MessageTemplate(
            id = "consulting_2",
            name = "Consulting Option 2",
            industry = "Consulting",
            message = "Thanks for calling! I'm on a conference call right now. Please text your details."
        ),
        MessageTemplate(
            id = "consulting_3",
            name = "Consulting Option 3",
            industry = "Consulting",
            message = "Hello! I am currently consulting with a business. Please text if you need to schedule a strategy session."
        ),
        MessageTemplate(
            id = "consulting_4",
            name = "Consulting Option 4",
            industry = "Consulting",
            message = "Hi there! I'm in the middle of a presentation. Please leave a text and I'll get back to you."
        ),
        MessageTemplate(
            id = "consulting_5",
            name = "Consulting Option 5",
            industry = "Consulting",
            message = "Sorry I missed your call! I'm traveling for business. Please text your contact details."
        ),
        MessageTemplate(
            id = "consulting_6",
            name = "Consulting Option 6",
            industry = "Consulting",
            message = "Hello! If you want to book a consulting call, please text me your email and I will send my calendar."
        ),
        MessageTemplate(
            id = "consulting_7",
            name = "Consulting Option 7",
            industry = "Consulting",
            message = "Thanks for reaching out! I'm busy finalizing a report. I'll call you back later this afternoon."
        ),
        MessageTemplate(
            id = "consulting_8",
            name = "Consulting Option 8",
            industry = "Consulting",
            message = "Hi! I'm currently unavailable. If you need info on my consulting services, please text me."
        ),
        MessageTemplate(
            id = "consulting_9",
            name = "Consulting Option 9",
            industry = "Consulting",
            message = "Hello! I am currently deep in focused work and can't take a call. Please text your message."
        ),
        MessageTemplate(
            id = "consulting_10",
            name = "Consulting Option 10",
            industry = "Consulting",
            message = "Thanks for calling! Please text me your name, company, and what you need help with."
        )
    )

    fun getTemplateById(id: String): MessageTemplate? {
        return templates.find { it.id == id }
    }

    fun getTemplatesByIndustry(): Map<String, List<MessageTemplate>> {
        return templates.groupBy { it.industry }
    }
}