import json

industries = {
    'Plumbing': [
        "Hi! I'm currently on a plumbing job and my hands are full. Please text me your address and a brief description of the issue.",
        "Thanks for calling! I am under a sink right now. Text me if it's an emergency leak and I'll get back to you ASAP.",
        "Hello, this is [Name]. I'm currently with a client. Please text me your plumbing issue and I'll return your message shortly.",
        "Hi there! I'm out in the field. Please leave a text with what you need repaired or installed and I'll call back.",
        "Sorry I missed your call! I'm currently driving between service calls. Please text your details.",
        "Hello! I am dealing with a water emergency right now. Please text your name and the issue you're facing.",
        "Thanks for reaching out! I'm currently busy with a pipe repair. I'll get back to you as soon as I finish.",
        "Hi! I'm currently unavailable. If you need a plumbing estimate, please text me your address and what you need done.",
        "Hello! I am currently on a service call. Please text 'PLUMBING' with your issue and I'll reply promptly.",
        "Thanks for calling! If this is a plumbing emergency, text 'URGENT' along with your address. Otherwise, I'll call you back soon."
    ],
    'Electrical': [
        "Hi! I'm currently working on an electrical panel and can't answer. Please text me your issue.",
        "Thanks for calling! I am dealing with high voltage right now. Please text your name and job details.",
        "Hello! I am currently on a service call. Please text me if you need an estimate or emergency repair.",
        "Hi there! My hands are tied up with wires right now. Please leave a text and I'll get right back to you.",
        "Sorry I missed you! I am currently driving to my next electrical job. Please text your details.",
        "Hello! If you have a power outage or emergency, please text 'URGENT' with your address.",
        "Thanks for reaching out! I'm busy with an installation. I'll call you back as soon as I'm done.",
        "Hi! I'm currently unavailable. For lighting or wiring estimates, please text me your requirements.",
        "Hello! I am on a job site and can't hear my phone. Please text your electrical issue.",
        "Thanks for calling! Please text me your name and address, and I will call you back shortly."
    ],
    'HVAC': [
        "Hi! I'm currently servicing an AC/Heating unit. Please text me your issue and I'll call you back.",
        "Thanks for calling! I'm up in an attic or on a roof right now. Please text your details.",
        "Hello! I am currently on an HVAC service call. Please text if you need a repair or maintenance.",
        "Hi there! I'm out in the field working on a system. Please leave a text and I'll get back to you.",
        "Sorry I missed your call! I'm driving between HVAC jobs. Please text your address and the problem.",
        "Hello! If your AC or heating is completely out, please text 'NO AC/HEAT' and your address.",
        "Thanks for reaching out! I'm busy with an installation. I'll get back to you as soon as it's running.",
        "Hi! I'm currently unavailable. If you need an estimate for a new system, please text me.",
        "Hello! I am on a noisy job site right now. Please text your HVAC issue.",
        "Thanks for calling! Please text me your name, address, and the issue with your unit."
    ],
    'Roofing': [
        "Hi! I'm currently up on a roof and can't safely take your call. Please text me your details.",
        "Thanks for calling! I'm inspecting a roof right now. Please text your address and the issue.",
        "Hello! I am currently on a roofing job. Please text if you need a repair or full replacement estimate.",
        "Hi there! I'm out in the field. Please leave a text and I'll call back when I'm on the ground.",
        "Sorry I missed your call! I'm driving to my next inspection. Please text your contact details.",
        "Hello! If you have an active roof leak, please text 'LEAK' along with your address.",
        "Thanks for reaching out! I'm busy with a roof installation. I'll get back to you soon.",
        "Hi! I'm currently unavailable. If you need an estimate, please text me your address.",
        "Hello! I am on a noisy job site right now. Please text your roofing issue.",
        "Thanks for calling! Please text me your name, address, and what roofing service you need."
    ],
    'Construction': [
        "Hi! Working with power tools and can't hear the phone! Please text me your project details.",
        "Thanks for calling! I'm currently on a construction site. Please text your name and what you need.",
        "Hello! I am currently busy with a build. Please text if you need an estimate or have questions.",
        "Hi there! I'm out in the field framing or finishing. Please leave a text and I'll get back to you.",
        "Sorry I missed your call! I'm driving between sites. Please text your contact details.",
        "Hello! If this is regarding an ongoing project, please text me the details.",
        "Thanks for reaching out! I'm busy with a client. I'll get back to you later today.",
        "Hi! I'm currently unavailable. If you need a remodel or construction estimate, please text me.",
        "Hello! The job site is too loud for a call right now. Please text your message.",
        "Thanks for calling! Please text me your name, address, and what you're looking to build."
    ],
    'Landscaping': [
        "Hi! I'm currently operating loud lawn equipment. Please text me your landscaping needs.",
        "Thanks for calling! I'm out in the field planting and pruning. Please text your address.",
        "Hello! I am currently on a landscaping job. Please text if you need maintenance or a new design.",
        "Hi there! My hands are covered in dirt right now! Please leave a text and I'll get back to you.",
        "Sorry I missed your call! I'm driving between properties. Please text your contact details.",
        "Hello! If you need urgent yard cleanup, please text 'CLEANUP' along with your address.",
        "Thanks for reaching out! I'm busy with an installation. I'll call you back when I'm finished.",
        "Hi! I'm currently unavailable. If you need an estimate for yard work, please text me.",
        "Hello! The mowers are running so I can't hear the phone. Please text your message.",
        "Thanks for calling! Please text me your name, address, and what landscaping services you need."
    ],
    'Painting': [
        "Hi! In the middle of a coat of paint! I'll call you back as soon as I can put the brush down.",
        "Thanks for calling! I'm up on a ladder painting right now. Please text your address and project details.",
        "Hello! I am currently on a painting job. Please text if you need an interior or exterior estimate.",
        "Hi there! I'm prepping a house for painting. Please leave a text and I'll get back to you.",
        "Sorry I missed your call! I'm driving between paint jobs. Please text your contact details.",
        "Hello! If this is regarding scheduling a paint job, please text me the details.",
        "Thanks for reaching out! I'm busy finishing a room. I'll get back to you later today.",
        "Hi! I'm currently unavailable. If you need a painting estimate, please text me.",
        "Hello! I am currently spraying and can't take a call. Please text your message.",
        "Thanks for calling! Please text me your name, address, and what you need painted."
    ],
    'Cleaning': [
        "Hi! Currently at a client's home cleaning. Please text me your name and when you're looking for a clean.",
        "Thanks for calling! My hands are full of cleaning supplies right now. Please text your details.",
        "Hello! I am currently on a cleaning job. Please text if you need a deep clean or regular service.",
        "Hi there! I'm vacuuming and can't hear the phone. Please leave a text and I'll get back to you.",
        "Sorry I missed your call! I'm driving between houses. Please text your contact details.",
        "Hello! If you need an urgent move-out clean, please text 'URGENT CLEAN' along with your address.",
        "Thanks for reaching out! I'm busy detailing a home. I'll call you back when I'm finished.",
        "Hi! I'm currently unavailable. If you need a cleaning estimate, please text me.",
        "Hello! I am currently organizing and cleaning. Please text your message.",
        "Thanks for calling! Please text me your name, address, and what cleaning services you need."
    ],
    'Pest Control': [
        "Hi! In the middle of a treatment. Please text me your name and what kind of pests you're seeing.",
        "Thanks for calling! I'm currently spraying a property. Please text your address and the issue.",
        "Hello! I am currently on a pest control job. Please text if you need an inspection or treatment.",
        "Hi there! I'm out in the field eliminating pests. Please leave a text and I'll get back to you.",
        "Sorry I missed your call! I'm driving between appointments. Please text your contact details.",
        "Hello! If you have an urgent pest issue like wasps or rodents, please text 'URGENT' and your address.",
        "Thanks for reaching out! I'm busy inspecting a home. I'll call you back soon.",
        "Hi! I'm currently unavailable. If you need a pest control estimate, please text me.",
        "Hello! I am currently wearing protective gear and can't take a call. Please text your message.",
        "Thanks for calling! Please text me your name, address, and what pests you need handled."
    ],
    'Handyman': [
        "Hi! I'm currently on a service call. Please text me a list of what you need fixed.",
        "Thanks for calling! I'm working on a repair right now. Please text your address and the issue.",
        "Hello! I am currently busy with a handyman job. Please text if you need a repair or installation.",
        "Hi there! I'm out in the field fixing things. Please leave a text and I'll get back to you.",
        "Sorry I missed your call! I'm driving to my next job. Please text your contact details.",
        "Hello! If you have an urgent repair need, please text 'URGENT' along with your address.",
        "Thanks for reaching out! I'm busy finishing a project. I'll call you back later.",
        "Hi! I'm currently unavailable. If you need an estimate for some repairs, please text me.",
        "Hello! I am on a noisy job site right now. Please text your message.",
        "Thanks for calling! Please text me your name, address, and what handyman services you need."
    ],
    'Auto Repair': [
        "Hi! I'm currently under a car right now! Please text me your name and the make/model of your vehicle.",
        "Thanks for calling! I'm covered in grease at the moment. Please text your details.",
        "Hello! I am currently diagnosing a vehicle. Please text if you need an appointment or estimate.",
        "Hi there! I'm in the shop working on a repair. Please leave a text and I'll get back to you.",
        "Sorry I missed your call! I'm test driving a vehicle. Please text your contact details.",
        "Hello! If your car is broken down and you need a tow to the shop, please text 'TOW' and your location.",
        "Thanks for reaching out! I'm busy with an engine repair. I'll call you back soon.",
        "Hi! I'm currently unavailable. If you need a quote for an auto repair, please text me.",
        "Hello! The shop is too loud to hear the phone right now. Please text your message.",
        "Thanks for calling! Please text me your name, vehicle info, and what repair you need."
    ],
    'Towing': [
        "Hi! Currently on a recovery. Please text your exact location and the vehicle type.",
        "Thanks for calling! I'm hooking up a vehicle right now. Please text your location and issue.",
        "Hello! I am currently towing a car. Please text if you need a tow, jumpstart, or lockout service.",
        "Hi there! I'm out on the road. Please leave a text with your location and I'll give you an ETA.",
        "Sorry I missed your call! I'm driving to a call. Please text your exact location.",
        "Hello! If this is an emergency tow from a highway, please text 'EMERGENCY' and your location.",
        "Thanks for reaching out! I'm busy loading a vehicle. I'll call you back immediately after.",
        "Hi! I'm currently unavailable. If you need a quote for a long-distance tow, please text me.",
        "Hello! I am currently securing a load and can't take a call. Please text your message.",
        "Thanks for calling! Please text me your name, location, and what towing service you need."
    ],
    'Locksmith': [
        "Hi! On a lockout call. Please text 'LOCKED OUT' with your location and I'll call you back.",
        "Thanks for calling! I'm currently picking a lock. Please text your address and the issue.",
        "Hello! I am currently on a locksmith job. Please text if you need an auto, home, or business unlock.",
        "Hi there! I'm out in the field making keys. Please leave a text and I'll get back to you.",
        "Sorry I missed your call! I'm driving to my next unlock. Please text your contact details.",
        "Hello! If this is an emergency lockout, please text 'URGENT' along with your exact location.",
        "Thanks for reaching out! I'm busy rekeying a house. I'll call you back when I'm finished.",
        "Hi! I'm currently unavailable. If you need an estimate for new locks, please text me.",
        "Hello! I am currently cutting a key and the machine is loud. Please text your message.",
        "Thanks for calling! Please text me your name, location, and what locksmith service you need."
    ],
    'Real Estate': [
        "Hi! Currently showing a property to a client. Please text me which property you're interested in.",
        "Thanks for calling! I'm at an open house right now. Please text your name and what you're looking for.",
        "Hello! I am currently in a closing meeting. Please text if you want to buy or sell a home.",
        "Hi there! I'm out showing homes. Please leave a text and I'll get back to you shortly.",
        "Sorry I missed your call! I'm driving between properties. Please text your contact details.",
        "Hello! If you want to schedule a viewing for a specific listing, please text me the address.",
        "Thanks for reaching out! I'm busy with a client consultation. I'll call you back later today.",
        "Hi! I'm currently unavailable. If you want a free home valuation, please text me your address.",
        "Hello! I am currently negotiating an offer and can't take a call. Please text your message.",
        "Thanks for calling! Please text me your name and whether you are looking to buy or sell."
    ],
    'Fitness': [
        "Hi! In the middle of a training session! I'll call you back as soon as I'm finished.",
        "Thanks for calling! I'm at the gym training a client right now. Please text your details.",
        "Hello! I am currently coaching a class. Please text if you need to schedule a session.",
        "Hi there! I'm in the middle of a workout. Please leave a text and I'll get back to you.",
        "Sorry I missed your call! I'm between training sessions. Please text your contact details.",
        "Hello! If you want to sign up for personal training, please text 'TRAIN' and your goals.",
        "Thanks for reaching out! I'm busy with a fitness assessment. I'll call you back soon.",
        "Hi! I'm currently unavailable. If you need info on my training packages, please text me.",
        "Hello! The gym music is too loud to hear the phone right now. Please text your message.",
        "Thanks for calling! Please text me your name and what your fitness goals are."
    ],
    'Photography': [
        "Hi! Currently on a shoot. Please text me your name and the date you're interested in booking.",
        "Thanks for calling! I'm behind the camera right now. Please text your event details.",
        "Hello! I am currently editing photos. Please text if you need to book a session.",
        "Hi there! I'm out in the field shooting. Please leave a text and I'll get back to you.",
        "Sorry I missed your call! I'm driving to a location shoot. Please text your contact details.",
        "Hello! If you are inquiring about wedding photography, please text 'WEDDING' and your date.",
        "Thanks for reaching out! I'm busy with a portrait session. I'll call you back later today.",
        "Hi! I'm currently unavailable. If you need a quote for a photoshoot, please text me.",
        "Hello! I am currently in the studio and can't take a call. Please text your message.",
        "Thanks for calling! Please text me your name, the type of shoot you need, and the date."
    ],
    'Delivery': [
        "Hi! On the road making deliveries. Please text me your name and any delivery instructions.",
        "Thanks for calling! I'm carrying a package right now. Please text your address and the issue.",
        "Hello! I am currently out for delivery. Please text if you need to check on a package.",
        "Hi there! I'm driving my route. Please leave a text and I'll get back to you at my next stop.",
        "Sorry I missed your call! I'm dropping off a delivery. Please text your contact details.",
        "Hello! If you need to change a delivery address, please text the tracking number and new address.",
        "Thanks for reaching out! I'm busy loading my truck. I'll call you back before I head out.",
        "Hi! I'm currently unavailable. If you need a quote for courier services, please text me.",
        "Hello! I am currently navigating traffic and can't take a call. Please text your message.",
        "Thanks for calling! Please text me your name, tracking number, or delivery details."
    ],
    'Beauty Salon': [
        "Hi! Currently with a client in the chair. Please text me your name and what time you're looking for.",
        "Thanks for calling! I'm in the middle of a color service right now. Please text your details.",
        "Hello! I am currently doing hair/nails. Please text if you need to book an appointment.",
        "Hi there! My hands are busy making someone beautiful! Please leave a text and I'll reply soon.",
        "Sorry I missed your call! I'm washing hair right now. Please text your contact details.",
        "Hello! If you need to cancel or reschedule, please text me at least 24 hours in advance.",
        "Thanks for reaching out! I'm busy with a bridal party. I'll call you back later today.",
        "Hi! I'm currently unavailable. If you need pricing for salon services, please text me.",
        "Hello! The blow dryers are running so I can't hear the phone. Please text your message.",
        "Thanks for calling! Please text me your name and what beauty service you would like to book."
    ],
    'Catering': [
        "Hi! In the kitchen! Please text me your name and the date of your event.",
        "Thanks for calling! I'm currently prepping food for an event. Please text your details.",
        "Hello! I am currently at a catering gig. Please text if you need a quote for an upcoming event.",
        "Hi there! I'm cooking up a storm right now! Please leave a text and I'll get back to you.",
        "Sorry I missed your call! I'm driving the catering van to an event. Please text your contact details.",
        "Hello! If you are checking on an order for today, please text 'ORDER' and your name.",
        "Thanks for reaching out! I'm busy serving guests. I'll call you back tomorrow morning.",
        "Hi! I'm currently unavailable. If you need a menu and pricing, please text me your email address.",
        "Hello! It's loud in the kitchen right now. Please text your message.",
        "Thanks for calling! Please text me your name, event date, and guest count for catering."
    ],
    'Consulting': [
        "Hi! Currently in a meeting with a client. Please text me a brief summary of what you'd like to discuss.",
        "Thanks for calling! I'm on a conference call right now. Please text your details.",
        "Hello! I am currently consulting with a business. Please text if you need to schedule a strategy session.",
        "Hi there! I'm in the middle of a presentation. Please leave a text and I'll get back to you.",
        "Sorry I missed your call! I'm traveling for business. Please text your contact details.",
        "Hello! If you want to book a consulting call, please text me your email and I will send my calendar.",
        "Thanks for reaching out! I'm busy finalizing a report. I'll call you back later this afternoon.",
        "Hi! I'm currently unavailable. If you need info on my consulting services, please text me.",
        "Hello! I am currently deep in focused work and can't take a call. Please text your message.",
        "Thanks for calling! Please text me your name, company, and what you need help with."
    ]
}

lines = []
lines.append("package com.mctb.autoreply.data\n")
lines.append("data class MessageTemplate(")
lines.append("    val id: String,")
lines.append("    val name: String,")
lines.append("    val industry: String,")
lines.append("    val message: String")
lines.append(")\n")
lines.append("object MessageTemplates {\n")
lines.append("    val templates = listOf(")

template_strings = []
for ind, msgs in industries.items():
    base_id = ind.lower().replace(' ', '_').replace('&', 'and')
    for i, msg in enumerate(msgs):
        clean_msg = msg.replace('"', '\\"')
        template_strings.append(f"""        MessageTemplate(
            id = "{base_id}_{i+1}",
            name = "{ind} Option {i+1}",
            industry = "{ind}",
            message = "{clean_msg}"
        )""")

lines.append(",\n".join(template_strings))
lines.append("    )\n")
lines.append("    fun getTemplateById(id: String): MessageTemplate? {")
lines.append("        return templates.find { it.id == id }")
lines.append("    }\n")
lines.append("    fun getTemplatesByIndustry(): Map<String, List<MessageTemplate>> {")
lines.append("        return templates.groupBy { it.industry }")
lines.append("    }")
lines.append("}")

with open("c:/Users/blunt/Desktop/programs/mctb/app/src/main/java/com/mctb/autoreply/data/MessageTemplate.kt", "w", encoding="utf-8") as f:
    f.write("\n".join(lines))
print("Generated templates!")
